    package com.example.myapplication;

    import java.io.Serializable;

    public class Pjesma implements Serializable {
        String putanja;
        String naslov;
        String trajanje;

        public Pjesma(String putanja, String naslov, String trajanje) {
            this.putanja = putanja;
            this.naslov = naslov;
            this.trajanje = trajanje;
        }

        public String getPath() {
            return putanja;
        }

        public void setPath(String putanja) {
            this.putanja = putanja;
        }

        public String getTitle() {
            return naslov;
        }

        public void setTitle(String naslov) {
            this.naslov = naslov;
        }

        public String getDuration() {
            return trajanje;
        }

        public void setDuration(String trajanje) {
            this.trajanje = trajanje;
        }
    }
