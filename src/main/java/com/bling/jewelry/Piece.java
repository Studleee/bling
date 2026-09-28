package com.bling.jewelry;

/** The kinds of jewelry, which body slot each goes in, and what each costs at the bench. */
public enum Piece {
	EARRINGS("earrings", BodySlot.EARS, 1, GemUse.OPTIONAL, 1),
	STUDS("studs", BodySlot.EARS, 1, GemUse.REQUIRED, 1),
	CHAIN("chain", BodySlot.NECK, 3, GemUse.OPTIONAL, 1),
	ICED_CHAIN("iced_chain", BodySlot.NECK, 3, GemUse.ICED, 6),
	BRACELET("bracelet", BodySlot.WRIST, 2, GemUse.OPTIONAL, 1),
	WATCH("watch", BodySlot.WRIST, 2, GemUse.NONE, 0),
	ICED_WATCH("iced_watch", BodySlot.WRIST, 2, GemUse.ICED, 4);

	/** How a piece uses the bench's gem slot. */
	public enum GemUse {
		/** The gem slot must be empty. */
		NONE,
		/** Plain, or with any one gem. */
		OPTIONAL,
		/** Needs any one gem. */
		REQUIRED,
		/** Covered in diamonds: needs {@link #gems()} diamonds. */
		ICED
	}

	private final String id;
	private final BodySlot slot;
	private final int ingots;
	private final GemUse gemUse;
	private final int gems;

	Piece(String id, BodySlot slot, int ingots, GemUse gemUse, int gems) {
		this.id = id;
		this.slot = slot;
		this.ingots = ingots;
		this.gemUse = gemUse;
		this.gems = gems;
	}

	public String id() {
		return id;
	}

	public BodySlot slot() {
		return slot;
	}

	public int ingots() {
		return ingots;
	}

	public GemUse gemUse() {
		return gemUse;
	}

	/** How many gems it uses up (0 for none, 1 for a set gem, more for iced out). */
	public int gems() {
		return gems;
	}

	/** Whether it comes in a version for each gem (as opposed to no gem, or always diamonds). */
	public boolean hasGemVariants() {
		return gemUse == GemUse.OPTIONAL || gemUse == GemUse.REQUIRED;
	}

	public boolean isWatch() {
		return this == WATCH || this == ICED_WATCH;
	}

	public boolean isIced() {
		return gemUse == GemUse.ICED;
	}
}
