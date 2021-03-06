package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.model.Slide;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Runs the powerpoint translator and examines the output for specific strings.
 * @author rdclark
 * Date: 5/19/11
 * Time: 11:11 AM
 */
public class PowerpointTranslatorTest {

    private PowerpointTranslator translator;
    private String html;
    private File slideshows;
    private File generatedSlideshow;

    @Before
    public void prepareTranslator() throws IOException, XMLStreamException, RecognitionException {
        slideshows = new File("slideshows");
        File explodedPresentation = new File("src/test/resources/TestPresentation");
        assert(explodedPresentation.isDirectory());
        translator = new PowerpointTranslator(explodedPresentation);
        html = translator.renderSlideshow();
        generatedSlideshow  = null;
    }

    @After
    public void cleanup() {
        if (generatedSlideshow != null) try {
            FileUtils.deleteDirectory(generatedSlideshow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void translatorCollectsAllSlides() throws XMLStreamException, RecognitionException, FileNotFoundException {
        List<Slide> slides = translator.getSlides();
        assertThat(slides, notNullValue());
        assertThat(slides.size(), equalTo(3));
    }

    @Test
    public void translatorGeneratesHTML() {
        assertThat(html, startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void htmlIncludesTitleSlide() {
        assertThat(html, containsString("Test Slide Title"));
    }

    @Test
    public void htmlIncludesBulletedSlide() {
        assertThat(html, containsString("Test Slide Headline"));
        assertThat(html, allOf(containsString("li>Bullet point 1</li>"), containsString("li>Bullet point 1</li>"), containsString("li>Bullet Point 3</li>")));
    }

    @Test
    public void htmlIncludesImageSlide() {
        assertThat(html, containsString("Image Slide Headline"));
    }

    @Test
    public void htmlIncludesCommonFooter() {
        assertThat(html, containsString("<h1>This is a footer</h1>"));
        assertThat(html, not(containsString("<h2>This is a footer</h2>")));
    }

    @Test
    public void htmlIncludesImage() {
        assertThat(html, containsString("<img src=\"images/image1.png\" />"));
    }

    @Test
    public void packagedSlideshowIncludesImageFiles() throws XMLStreamException, RecognitionException, IOException {
        generatedSlideshow = translator.packageSlideshow(slideshows);
        assertThat(generatedSlideshow.isDirectory(), equalTo(true));
        File images = new File(generatedSlideshow, "images");
        assertThat(images.isDirectory(), equalTo(true));
        File imageFile = new File(images, "image1.png");
        assertThat(imageFile.exists(), equalTo(true));
    }


}
