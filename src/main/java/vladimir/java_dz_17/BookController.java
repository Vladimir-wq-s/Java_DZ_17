package vladimir.java_dz_17;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostConstruct
    public void initData() {
        bookRepository.save(new Book(null, "The Hobbit", "J.R.R. Tolkien", 1937, "Fantasy", 310, "A classic fantasy novel about Bilbo Baggins."));
        bookRepository.save(new Book(null, "1884", "George Orwell", 1949, "Dystopian", 328, "A dystopian social science fiction story about totalitarianism."));
        bookRepository.save(new Book(null, "The Shining", "Stephen King", 1977, "Horror", 447, "A psychological horror story set in a haunted hotel."));
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Book not found"));
        }
        bookRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setReleaseYear(updatedBook.getReleaseYear());
                    book.setStyle(updatedBook.getStyle());
                    book.setPageCount(updatedBook.getPageCount());
                    book.setDescription(updatedBook.getDescription());
                    return ResponseEntity.ok(bookRepository.save(book));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) Integer pageCount,
            @RequestParam(required = false) String keyword) {

        return bookRepository.findAll().stream()
                .filter(b -> title == null || b.getTitle().equalsIgnoreCase(title))
                .filter(b -> author == null || b.getAuthor().equalsIgnoreCase(author))
                .filter(b -> releaseYear == null || b.getReleaseYear().equals(releaseYear))
                .filter(b -> style == null || b.getStyle().equalsIgnoreCase(style))
                .filter(b -> pageCount == null || b.getPageCount().equals(pageCount))
                .filter(b -> keyword == null || (b.getDescription() != null && b.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }
}
