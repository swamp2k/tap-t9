package io.github.sspanak.tt9.ui.main.keys;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import io.github.sspanak.tt9.R;
import io.github.sspanak.tt9.ime.TraditionalT9;
import io.github.sspanak.tt9.ime.modes.InputModeKind;
import io.github.sspanak.tt9.util.Logger;

/**
 * Tracks a finger moving across the existing 3x4 T9 keypad.
 *
 * This deliberately does not try to decode gesture geometry yet. The MVP records the ordered
 * sequence of number-key zones entered by the pointer and hands that sequence to TraditionalT9
 * for dictionary lookup. Normal taps remain fully handled by the existing key classes.
 */
final class T9GlideGesture {
	private static final String LOG_TAG = T9GlideGesture.class.getSimpleName();

	private static final int[] WORD_KEY_IDS = {
		R.id.soft_key_2,
		R.id.soft_key_3,
		R.id.soft_key_4,
		R.id.soft_key_5,
		R.id.soft_key_6,
		R.id.soft_key_7,
		R.id.soft_key_8,
		R.id.soft_key_9
	};

	@NonNull private final SoftKeyNumberSwipeable sourceKey;
	@NonNull private final StringBuilder sequence = new StringBuilder();

	private boolean tracking = false;
	private boolean gliding = false;


	T9GlideGesture(@NonNull SoftKeyNumberSwipeable sourceKey) {
		this.sourceKey = sourceKey;
	}


	boolean onTouch(@NonNull TraditionalT9 tt9, @NonNull MotionEvent event) {
		if (!isAvailable(tt9)) {
			reset();
			return false;
		}

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				start();
				return false;

			case MotionEvent.ACTION_MOVE:
				if (!tracking) {
					return false;
				}
				appendKeyAt(event.getRawX(), event.getRawY());
				if (gliding) {
					sourceKey.cancelTouchForT9Glide();
				}
				return gliding;

			case MotionEvent.ACTION_UP:
				if (!tracking) {
					return false;
				}
				appendKeyAt(event.getRawX(), event.getRawY());
				final boolean wasGliding = gliding;
				final String completedSequence = sequence.toString();
				reset();
				if (wasGliding) {
					sourceKey.cancelTouchForT9Glide();
					Logger.d(LOG_TAG, "Completed T9 glide sequence: " + completedSequence);
					tt9.onT9Glide(completedSequence);
				}
				return wasGliding;

			case MotionEvent.ACTION_CANCEL:
				final boolean wasTrackingGlide = gliding;
				reset();
				if (wasTrackingGlide) {
					sourceKey.cancelTouchForT9Glide();
				}
				return wasTrackingGlide;

			default:
				return gliding;
		}
	}


	private boolean isAvailable(@NonNull TraditionalT9 tt9) {
		final int number = sourceKey.getNumber();

		return
			number >= 2 && number <= 9
			&& !sourceKey.isFnPanelOn()
			&& !tt9.isVoiceInputActive()
			&& InputModeKind.isPredictive(tt9.getInputMode());
	}


	private void start() {
		tracking = true;
		gliding = false;
		sequence.setLength(0);
		append(sourceKey.getNumber());
	}


	private void appendKeyAt(float rawX, float rawY) {
		final int number = findNumberAt(rawX, rawY);
		if (number < 2 || number > 9) {
			return;
		}

		final int previous = lastNumber();
		if (number == previous) {
			return;
		}

		append(number);
		gliding = sequence.length() > 1;
	}


	private int findNumberAt(float rawX, float rawY) {
		final View root = sourceKey.getRootView();
		if (root == null) {
			return -1;
		}

		final int[] location = new int[2];
		for (int keyId : WORD_KEY_IDS) {
			final View key = root.findViewById(keyId);
			if (!(key instanceof SoftKeyNumberNumpad) || key.getVisibility() != View.VISIBLE) {
				continue;
			}

			key.getLocationOnScreen(location);
			final float left = location[0];
			final float top = location[1];
			final float right = left + key.getWidth();
			final float bottom = top + key.getHeight();

			if (rawX >= left && rawX <= right && rawY >= top && rawY <= bottom) {
				return ((SoftKeyNumberNumpad) key).getNumber();
			}
		}

		return -1;
	}


	private void append(int number) {
		if (number >= 2 && number <= 9) {
			sequence.append(number);
		}
	}


	private int lastNumber() {
		return sequence.length() == 0 ? -1 : sequence.charAt(sequence.length() - 1) - '0';
	}


	private void reset() {
		tracking = false;
		gliding = false;
		sequence.setLength(0);
	}
}
