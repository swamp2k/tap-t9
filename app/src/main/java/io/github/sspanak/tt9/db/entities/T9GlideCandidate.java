package io.github.sspanak.tt9.db.entities;

import androidx.annotation.NonNull;

public class T9GlideCandidate {
	@NonNull public final String word;
	@NonNull public final String sequence;
	public final int frequency;

	public T9GlideCandidate(@NonNull String word, @NonNull String sequence, int frequency) {
		this.word = word;
		this.sequence = sequence;
		this.frequency = frequency;
	}
}
