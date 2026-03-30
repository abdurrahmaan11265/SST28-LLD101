import java.util.ArrayList;
import java.util.List;

public class Movie {
    private final String movieId;
    private final String title;
    private final int durationMins;
    private final String genre;
    private final String language;
    private final String description;
    private final List<Show> shows;

    public Movie(String movieId, String title, int durationMins,
                 String genre, String language, String description) {
        this.movieId = movieId;
        this.title = title;
        this.durationMins = durationMins;
        this.genre = genre;
        this.language = language;
        this.description = description;
        this.shows = new ArrayList<>();
    }

    public void addShow(Show show) {
        shows.add(show);
    }

    public List<Show> getShows() {
        return shows;
    }

    public String getMovieId()     { return movieId; }
    public String getTitle()       { return title; }
    public int getDurationMins()   { return durationMins; }
    public String getGenre()       { return genre; }
    public String getLanguage()    { return language; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return title + " (" + language + ", " + durationMins + " min)";
    }
}
