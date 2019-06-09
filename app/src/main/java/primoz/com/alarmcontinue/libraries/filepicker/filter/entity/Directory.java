package primoz.com.alarmcontinue.libraries.filepicker.filter.entity;

import java.util.ArrayList;
import java.util.List;

public class Directory<T> {
    private String id;
    private String name;
    private String path;
    private List<T> files = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getFiles() {
        return files;
    }

    public void setFiles(List<T> files) {
        this.files = files;
    }

    public void addFile(T file) {
        files.add(file);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Directory)) return false;
        Directory directory = (Directory) o;
        return this.path.equals(directory.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
