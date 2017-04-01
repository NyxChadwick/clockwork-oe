package uk.org.hekate.xml;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

public final class XmlNodes {
    private XmlNodes() { }


    private static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
        private final int _length;
        @NotNull private final NodeList _list;

        NodeListWrapper(@NotNull NodeList list) {
            _list = list;
            _length = list.getLength();
        }

        @NotNull public Node get(int index) { return _list.item(index); }

        public int size() { return _length; }
    }


    @NotNull public static List<Node> asList(NodeList n) {
        return (n == null || n.getLength()==0)? Collections.emptyList(): new NodeListWrapper(n);
    }
}
