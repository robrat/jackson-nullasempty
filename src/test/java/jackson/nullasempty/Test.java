package jackson.nullasempty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class JacksonManagedRefTest {

    static class Parent {
        private String name;

        @JsonManagedReference
        private List<Item> children = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Item> getChildren() {
            return children;
        }

        public void setChildren(List<Item> children) {
            this.children = children;
        }
    }

    static class Item {
        private String name;

        @JsonBackReference
        private Parent parent;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }
    }

    @Test
    void test_nonEmptyChildren() throws Exception {
        ObjectMapper om = new ObjectMapper();

        String str = "{ \"name\": \"parent\", \"children\":[{\"name\":\"child1\"},{\"name\":\"child1\"}]}";
        Parent obj = om.readValue(str, Parent.class);
        assertNotNull(obj.children);
    }

    // java.lang.IllegalStateException: Should never try to reset delegate
    @Test
    void test_nullChildren() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.configOverride(List.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

        String str = "{ \"name\": \"parent\", \"children\": null }";
        Parent obj = om.readValue(str, Parent.class);
        assertNotNull(obj.children);
    }

    // not null assertion fails
    @Test
    void test_nullChildren_contentNullConfig() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.configOverride(List.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        om.configOverride(List.class).setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY));

        String str = "{ \"name\": \"parent\", \"children\": null }";
        Parent obj = om.readValue(str, Parent.class);
        assertNotNull(obj.children);
    }
}
