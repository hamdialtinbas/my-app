package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.mycompany.myapp.domain.Book;

import com.mycompany.myapp.domain.PACIDemoSignedDocType;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.xjc.api.Mapping;
import com.sun.tools.internal.xjc.api.S2JJAXBModel;
import com.sun.tools.internal.xjc.api.SchemaCompiler;
import com.sun.tools.internal.xjc.api.XJC;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Book.
 */
@RestController
@RequestMapping("/api")
public class BookResource {

    private final Logger log = LoggerFactory.getLogger(BookResource.class);

    private static final String ENTITY_NAME = "book";

    private final BookRepository bookRepository;

    public BookResource(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * POST  /books : Create a new book.
     *
     * @param book the book to create
     * @return the ResponseEntity with status 201 (Created) and with body the new book, or with status 400 (Bad Request) if the book has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/books")
    @Timed
    public ResponseEntity<Book> createBook(@RequestBody Book book) throws URISyntaxException {
        log.debug("REST request to save Book : {}", book);
        if (book.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new book cannot already have an ID")).body(null);
        }
        Book result = bookRepository.save(book);
        return ResponseEntity.created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /books : Updates an existing book.
     *
     * @param book the book to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated book,
     * or with status 400 (Bad Request) if the book is not valid,
     * or with status 500 (Internal Server Error) if the book couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/books")
    @Timed
    public ResponseEntity<Book> updateBook(@RequestBody Book book) throws URISyntaxException {
        log.debug("REST request to update Book : {}", book);
        if (book.getId() == null) {
            return createBook(book);
        }
        Book result = bookRepository.save(book);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, book.getId().toString()))
            .body(result);
    }

    /**
     * GET  /books : get all the books.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of books in body
     */
    @GetMapping("/books")
    @Timed
    public List<Book> getAllBooks() {
        log.debug("REST request to get all Books");
        List<Book> books = bookRepository.findAll();
        return books;
    }

    /**
     * GET  /books/:id : get the "id" book.
     *
     * @param id the id of the book to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the book, or with status 404 (Not Found)
     */
    @GetMapping("/books/{id}")
    @Timed
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        log.debug("REST request to get Book : {}", id);
        Book book = bookRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(book));
    }

    /**
     * DELETE  /books/:id : delete the "id" book.
     *
     * @param id the id of the book to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/books/{id}")
    @Timed
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.debug("REST request to delete Book : {}", id);
        bookRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/create")
    @Timed
    public void createObject() throws IOException {
        PACIDemoSignedDocType d = new PACIDemoSignedDocType();

        Gson gson = new Gson();
        gson.toJson(d);

        System.out.println(gson.toJson(d));

    }

    @GetMapping("/createJacksonObject")
    @Timed
    public void createJacksonObject() throws IOException {


        String outputDirectory = "src/main/java/domain/Food.xsd";
        SchemaCompiler sc = XJC.createSchemaCompiler();

        File schemaFile = new File(outputDirectory);
        InputSource is = new InputSource(schemaFile.toURI().toString());

        sc.parseSchema(is);
        S2JJAXBModel model = sc.bind();
        JCodeModel jCodeModel = model.generateCode(null, null);
        jCodeModel.build(new File(outputDirectory));
        for (Mapping m : model.getMappings()) {
            writeToStandardOutputWithDeprecatedJsonSchema(createJaxbObjectMapper(), m.getType()
                .getTypeClass()
                .fullName());

        }
    }
     private void writeToStandardOutputWithDeprecatedJsonSchema(final ObjectMapper mapper,
                                                                      final String fullyQualifiedClassName) {
        try {
            final JsonSchema jsonSchema = mapper.generateJsonSchema(Class.forName(fullyQualifiedClassName));
            System.out.println(jsonSchema);
        } catch (ClassNotFoundException cnfEx) {
            System.err.println("Unable to find class " + fullyQualifiedClassName);
        } catch (JsonMappingException jsonEx) {
            System.err.println("Unable to map JSON: " + jsonEx);
        }
    }

     private ObjectMapper createJaxbObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        return mapper;
    }

    /**
    @RequestMapping("/samplexml2" produces = MediaType.APPLICATION_JSON_VALUE)
    public SampleXML CreateXMLFile2 () throws  FileNotFoundException {
        try {

            String requestUrl = null;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ResponseErrorHandler());

            String decodedUrl =   "http://localhost:8080/sample.xml";
            String response = restTemplate.getForObject(decodedUrl, String.class);

            //Prepare JAXB objects
            JAXBContext jaxbContext = JAXBContext.newInstance(SampleXML.class);
            Unmarshaller u = jaxbContext.createUnmarshaller();

            //Create an XMLReader to use with our filter
            XMLReader reader = XMLReaderFactory.createXMLReader();

            //Create the filter (to add namespace) and set the xmlReader as its parent.
            NamespaceFilter inFilter = new NamespaceFilter("http://webservices.amazon.com/AWSECommerceService/2011-08-01", true);
            inFilter.setParent(reader);

            //Prepare the input, in this case a java.io.File (output)
            InputSource is = new InputSource(new StringReader(response));

            //Create a SAXSource specifying the filter
            SAXSource source = new SAXSource(inFilter, is);

            //Do unmarshalling
            SampleXML myJaxbObject = (SampleXML) u.unmarshal(source);

            //Convert to myJaxbObject to JSON string here;

            return myJaxbObject;

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
    **/

    @GetMapping("/createObj")
    @Timed
    public void createObj() throws IOException, JAXBException {

        JAXBContext jc = JAXBContext.newInstance(PACIDemoSignedDocType.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty("eclipselink.media-type", "application/json");
        File json = new File("src/main/java/domain/input.json");
        Object myResult = unmarshaller.unmarshal(json);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty("eclipselink.media-type", "application/json");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(myResult, System.out);


    }

}



