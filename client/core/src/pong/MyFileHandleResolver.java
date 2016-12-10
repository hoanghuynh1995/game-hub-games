package pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by hoang on 11/27/2016.
 */

public class MyFileHandleResolver implements FileHandleResolver {
    @Override
    public FileHandle resolve(String fileName) {
        if (Gdx.files.external(fileName).exists())
            return Gdx.files.external(fileName);
        else if(Gdx.files.local("Pong" + "/" + fileName).exists())
            return Gdx.files.local("Pong" + "/" + fileName);
        else
            return Gdx.files.internal(fileName);
    }
}
