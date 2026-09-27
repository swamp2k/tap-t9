package io.github.sspanak.tt9.ime.glide;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

/**
 * Pure-Java helpers for T9 glide decoding.
 *
 * A physical glide only tells us which key zones were visited. Consecutive letters that live on
 * the same T9 key are therefore collapsed (for example, 4663 may arrive as 463). The decoder
 * expands a small, bounded set of plausible repeated-key variants and ranks the dictionary matches.
 */
public final class T9GlideDecoder {
	public static final int MAX_EXTRA_REPEATS = 3;
	public static final int MAX_SEQUENCE_VARIANTS = 48;
	public static final int MAX_WORD_LENGTH = 16;
	public static final int MAX_RESULTS = 12;

	private T9GlideDecoder() {}


	@NonNull
	public static ArrayList<String> generateSequenceVariants(@NonNull String compressedSequence) {
		LinkedHashSet<String> seen = new LinkedHashSet<>();
		ArrayDeque<Variant> queue = new ArrayDeque<>();

		seen.add(compressedSequence);
		queue.add(new Variant(compressedSequence, 0));

		while (!queue.isEmpty() && seen.size() < MAX_SEQUENCE_VARIANTS) {
			Variant current = queue.removeFirst();
			if (current.extraRepeats >= MAX_EXTRA_REPEATS || current.sequence.length() >= MAX_WORD_LENGTH) {
				continue;
			}

			for (int i = 0; i < current.sequence.length() && seen.size() < MAX_SEQUENCE_VARIANTS; i++) {
				char digit = current.sequence.charAt(i);
				if (digit < '2' || digit > '9') {
					continue;
				}

				String expanded =
					current.sequence.substring(0, i + 1)
					+ digit
					+ current.sequence.substring(i + 1);

				if (seen.add(expanded)) {
					queue.addLast(new Variant(expanded, current.extraRepeats + 1));
				}
			}
		}

		return new ArrayList<>(seen);
	}


	@NonNull
	public static ArrayList<T9GlideCandidate> rank(
		@NonNull String compressedSequence,
		@NonNull ArrayList<T9GlideCandidate> candidates
	) {
		Map<String, T9GlideCandidate> bestByWord = new LinkedHashMap<>();

		for (T9GlideCandidate candidate : candidates) {
			String key = candidate.word.toLowerCase(Locale.ROOT);
			T9GlideCandidate previous = bestByWord.get(key);
			if (previous == null || compareCandidate(candidate, previous, compressedSequence) < 0) {
				bestByWord.put(key, candidate);
			}
		}

		ArrayList<T9GlideCandidate> ranked = new ArrayList<>(bestByWord.values());
		ranked.sort((a, b) -> compareCandidate(a, b, compressedSequence));

		if (ranked.size() > MAX_RESULTS) {
			return new ArrayList<>(ranked.subList(0, MAX_RESULTS));
		}

		return ranked;
	}


	private static int compareCandidate(
		@NonNull T9GlideCandidate a,
		@NonNull T9GlideCandidate b,
		@NonNull String compressedSequence
	) {
		int byFrequency = Integer.compare(b.frequency, a.frequency);
		if (byFrequency != 0) {
			return byFrequency;
		}

		int aRepeats = Math.max(0, a.sequence.length() - compressedSequence.length());
		int bRepeats = Math.max(0, b.sequence.length() - compressedSequence.length());
		int byRepeats = Integer.compare(aRepeats, bRepeats);
		if (byRepeats != 0) {
			return byRepeats;
		}

		int byLength = Integer.compare(a.word.length(), b.word.length());
		if (byLength != 0) {
			return byLength;
		}

		return a.word.compareToIgnoreCase(b.word);
	}


	private static final class Variant {
		@NonNull final String sequence;
		final int extraRepeats;

		Variant(@NonNull String sequence, int extraRepeats) {
			this.sequence = sequence;
			this.extraRepeats = extraRepeats;
		}
	}
}
