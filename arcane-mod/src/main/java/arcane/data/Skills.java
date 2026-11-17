package arcane.data;

/**
 * Описание всех навыков.
 * Классы: 0 = Маг, 1 = Мечник, 2 = Лучник.
 * Маг: 0 Сила магии, 1 Быстрая магия, 2 Молния, 3 Телепорт, 4 Исцеление, 5 Ледяная волна, 6 Мудрость.
 * Мечник: 7 Сила клинка, 8 Железная кожа, 9 Живучесть, 10 Жажда крови, 11 Вихрь клинков.
 * Лучник: 12 Меткость, 13 Лёгкие ноги, 14 Лёгкое падение, 15 Огненные стрелы, 16 Азарт охотника.
 */
public final class Skills {
	private Skills() {
	}

	public static final int POWER = 0;
	public static final int HASTE = 1;
	public static final int LIGHTNING = 2;
	public static final int TELEPORT = 3;
	public static final int HEAL = 4;
	public static final int FROST = 5;
	public static final int WISDOM = 6;
	public static final int BLADE = 7;
	public static final int IRON = 8;
	public static final int VITALITY = 9;
	public static final int BLOOD = 10;
	public static final int WHIRL = 11;
	public static final int SHARP = 12;
	public static final int SWIFT = 13;
	public static final int LIGHT = 14;
	public static final int FIRE = 15;
	public static final int RUSH = 16;

	public static final int COUNT = 17;
	public static final int CLASS_COUNT = 3;
	public static final int SPELL_COUNT = 5;

	private static final int[] MAX_RANK = {5, 3, 1, 1, 1, 1, 3, 5, 4, 5, 3, 1, 5, 3, 3, 1, 3};
	private static final int[] REQ_LEVEL = {1, 1, 2, 3, 4, 5, 2, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5};
	private static final int[] CLASS_FIRST = {0, 7, 12};
	private static final int[] CLASS_SIZE = {7, 5, 5};

	public static int maxRank(int skill) {
		return skill >= 0 && skill < COUNT ? MAX_RANK[skill] : 0;
	}

	public static int requiredLevel(int skill) {
		return skill >= 0 && skill < COUNT ? REQ_LEVEL[skill] : 99;
	}

	/** Номер первого навыка класса. */
	public static int first(int cls) {
		return CLASS_FIRST[cls];
	}

	/** Сколько навыков у класса. */
	public static int size(int cls) {
		return CLASS_SIZE[cls];
	}
}
