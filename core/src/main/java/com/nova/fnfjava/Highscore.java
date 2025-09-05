package com.nova.fnfjava;

/**
 * A core class which handles tracking score and combo for the current song.
 */
public class Highscore {
    /**
     * Keeps track of notes hit for the current song
     * and how accurate you were with each note (bad, missed, shit, etc.)
     */
    public static Tallies tallies = new Tallies();

    /**
     * Keeps track of notes hit for the current WEEK / level
     * for use with storymode, or likely any other "playlist" esque option
     */
    public static Tallies talliesLevel = new Tallies();

    /**
     * A structure object containing the data for highscore tallies.
     */
    public static class Tallies {
        public int combo;

        /**
         * How many notes you let scroll by.
         */
        public int missed;

        public int shit;
        public int bad;
        public int good;
        public int sick;
        public int maxCombo;

        public int score;

        public boolean isNewHighscore;

        /**
         * How many notes total that you hit. (NOT how many notes total in the song!)
         */
        public int totalNotesHit;

        /**
         * How many notes in the current chart
         */
        public int totalNotes;

        public Tallies() {
            this.combo = 0;
            this.missed = 0;
            this.shit = 0;
            this.bad = 0;
            this.good = 0;
            this.sick = 0;
            this.maxCombo = 0;
            this.score = 0;
            this.isNewHighscore = false;
            this.totalNotesHit = 0;
            this.totalNotes = 0;
        }
    }
}
