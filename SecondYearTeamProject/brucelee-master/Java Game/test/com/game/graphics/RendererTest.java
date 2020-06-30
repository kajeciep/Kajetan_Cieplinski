package com.game.graphics;

import com.game.UI.JavaFXTestingRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

public class RendererTest {

    Renderer rend;

    @Rule
    public JavaFXTestingRule javafxRule = new JavaFXTestingRule();

    @Before
    public void createTestingRenderer(){
        rend = new Renderer(1);
    }

    @Test
    public void fileImportTest(){
        rend.imageHash = new HashMap<>();
        File oneImage = new File("Java Game/test/res/oneImage");
        File oneAudio = new File("Java Game/test/res/oneAudio");
        File bothImageAudio = new File("Java Game/test/res/bothImageAudio");
        assert(rend.soundHash.size() == 0);
        assert(rend.imageHash.size() == 0);
        rend.importFiles(oneImage);
        System.out.println(rend.soundHash.size());
        assert(rend.soundHash.size() == 0);
        assert(rend.imageHash.size() == 1);
        rend.importFiles(oneAudio);
        System.out.println(rend.soundHash.size());
        assert(rend.soundHash.size() == 1);
        assert(rend.imageHash.size() == 1);
        rend.importFiles(bothImageAudio);
        System.out.println(rend.soundHash.size());
        assert(rend.soundHash.size() == 2);
        assert(rend.imageHash.size() == 2);
    }
}