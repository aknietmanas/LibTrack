package com.libtrack.model;

import javafx.beans.property.*;


public class Book {

    private final IntegerProperty bookId;
    private final StringProperty isbn;
    private final StringProperty title;
    private final IntegerProperty authorId;
    private final StringProperty authorName; // Для отображения
    private final StringProperty genre;
    private final StringProperty publisher;
    private final IntegerProperty publicationYear;
    private final IntegerProperty pages;
    private final IntegerProperty copiesTotal;
    private final IntegerProperty copiesAvailable;
    private final StringProperty description;

    public Book() {
        this(0, "", "", 0, "", "", "", 0, 0, 0, 0, "");
    }

    public Book(int bookId, String isbn, String title, int authorId,
                String authorName, String genre, String publisher,
                int publicationYear, int pages, int copiesTotal,
                int copiesAvailable, String description) {
        this.bookId = new SimpleIntegerProperty(bookId);
        this.isbn = new SimpleStringProperty(isbn);
        this.title = new SimpleStringProperty(title);
        this.authorId = new SimpleIntegerProperty(authorId);
        this.authorName = new SimpleStringProperty(authorName);
        this.genre = new SimpleStringProperty(genre);
        this.publisher = new SimpleStringProperty(publisher);
        this.publicationYear = new SimpleIntegerProperty(publicationYear);
        this.pages = new SimpleIntegerProperty(pages);
        this.copiesTotal = new SimpleIntegerProperty(copiesTotal);
        this.copiesAvailable = new SimpleIntegerProperty(copiesAvailable);
        this.description = new SimpleStringProperty(description);
    }


    public int getBookId() { return bookId.get(); }
    public void setBookId(int value) { bookId.set(value); }
    public IntegerProperty bookIdProperty() { return bookId; }

    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String value) { isbn.set(value); }
    public StringProperty isbnProperty() { return isbn; }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public int getAuthorId() { return authorId.get(); }
    public void setAuthorId(int value) { authorId.set(value); }
    public IntegerProperty authorIdProperty() { return authorId; }

    public String getAuthorName() { return authorName.get(); }
    public void setAuthorName(String value) { authorName.set(value); }
    public StringProperty authorNameProperty() { return authorName; }

    public String getGenre() { return genre.get(); }
    public void setGenre(String value) { genre.set(value); }
    public StringProperty genreProperty() { return genre; }

    public String getPublisher() { return publisher.get(); }
    public void setPublisher(String value) { publisher.set(value); }
    public StringProperty publisherProperty() { return publisher; }

    public int getPublicationYear() { return publicationYear.get(); }
    public void setPublicationYear(int value) { publicationYear.set(value); }
    public IntegerProperty publicationYearProperty() { return publicationYear; }

    public int getPages() { return pages.get(); }
    public void setPages(int value) { pages.set(value); }
    public IntegerProperty pagesProperty() { return pages; }

    public int getCopiesTotal() { return copiesTotal.get(); }
    public void setCopiesTotal(int value) { copiesTotal.set(value); }
    public IntegerProperty copiesTotalProperty() { return copiesTotal; }

    public int getCopiesAvailable() { return copiesAvailable.get(); }
    public void setCopiesAvailable(int value) { copiesAvailable.set(value); }
    public IntegerProperty copiesAvailableProperty() { return copiesAvailable; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }


    public boolean isAvailable() {
        return copiesAvailable.get() > 0;
    }


    public String getStatus() {
        return isAvailable() ? "Доступна" : "Занята";
    }

    @Override
    public String toString() {
        return title.get() + " - " + authorName.get();
    }
}