package uk.org.hekate.utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;


public class Json {
    public <TModel> TModel load(@NotNull String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<TModel>(){}.getType();
            TModel model = new Gson().fromJson(reader, type);

            return model;
        }
    }


    public <TModel> void save(@NotNull String filename, @NotNull TModel model) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            new Gson().toJson(model, writer);
        }
    }
}
