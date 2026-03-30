public class Admin {
    private final String adminId;

    public Admin(String adminId) {
        this.adminId = adminId;
    }

    public void addMovie(BookingService service, Movie movie) {
        service.addMovie(movie);
        System.out.println("[Admin] Movie added: " + movie.getTitle());
    }

    public void addTheater(BookingService service, Theater theater) {
        service.addTheater(theater);
        System.out.println("[Admin] Theater added: " + theater.getName());
    }

    public void scheduleShow(BookingService service, Show show) {
        service.addShow(show);
        System.out.println("[Admin] Show scheduled: "
                + show.getMovie().getTitle() + " at " + show.getStartTime());
    }

    public void cancelShow(BookingService service, String showId) {
        service.cancelShow(showId);
        System.out.println("[Admin] Show cancelled: " + showId);
    }

    public String getAdminId() { return adminId; }
}
