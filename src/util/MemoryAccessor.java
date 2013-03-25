package util;

import exceptions.ShouldNeverHappenException;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Allows to access and modify object's fields in runtime
 * (unless access modifiers prevent it).
 * 
 * @author Jaroslaw Pawlak
 */
public class MemoryAccessor extends JPanel {

    private JComponent[][] component;

    private Object object;

    public MemoryAccessor() {
        super(new GridBagLayout());
    }
    
    public MemoryAccessor(Object object) {
        super(new GridBagLayout());
        setObject(object);
    }

    private void createLayout() {
        this.removeAll();
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 3, 3);

        for (int i = 0; i < component.length; i++) {
            for (int j = 0; j < component[i].length; j++) {
                c.gridx = j;
                c.gridy = i;
                add(component[i][j], c);
            }
        }
        
        if (!isEnabled()) {
            for (Component comp : getComponents()) {
                comp.setEnabled(false);
            }
        }
    }

    /**
     * Sets the object and recreates layout.
     */
    public final void setObject(Object object) {

        this.object = object;
        
        String title = getTitle() != null? getTitle()
                                            : object.getClass().getSimpleName();
        
        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), title,
                TitledBorder.CENTER, TitledBorder.TOP));

        Field[] fields = object.getClass().getDeclaredFields();
        component = new JComponent[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            try {
                component[i] = processField(fields[i], object);
            } catch (IllegalAccessException ex) {
                System.out.println("This shouldn't happen: " + ex);
                // unless handling protected and friendly access modifiers
                // has been implemented
            }
        }
        
        createLayout();
    }

    /**
     * Defines a title of this JPanel's titled border. Override it to customise
     * a title. Object's class' simple name will be used if it returns null
     * (default behaviour).
     * <p>
     * If you override this method, you may need to use a default constructor
     * (with no arguments) and then call setObject(Object).
     */
    public String getTitle() {
        return null;
    }

    /**
     * //TODO handle other keywords (at least final & static)
     * 
     * @throws IllegalAccessException 
     *         protected and friendly (no-modifier) access modifiers are
     *         not supported, hence this exception should never be thrown
     */
    private JComponent[] processField(final Field field, final Object object)
                                                throws IllegalAccessException {
        int arrayLength = -1;
        boolean isArray = false;
        boolean isPublic = false;

        // get access modifier
        String accessModifer;
        if (Modifier.isPublic(field.getModifiers())) {
            accessModifer = "public";
            isPublic = true;
        } else if (Modifier.isProtected(field.getModifiers())) {
            accessModifer = "protected";
            System.out.println(this.getClass()
                    + " protected access modifier not yet supported");
        } else if (Modifier.isPrivate(field.getModifiers())) {
            accessModifer = "private";
        } else {
            accessModifer = "friendly";
            System.out.println(this.getClass()
                    + " friendly access modifier not yet supported");
        }

        // get type
        String type;
        if (field.getType().isArray()) {
            if (field.getType().getComponentType().isArray()) {
                throw new UnsupportedOperationException(
                        "Multi-dimensional arrays not yet supported");
            }

            isArray = true;
            if (isPublic) {
                arrayLength = getArrayLength(field.get(object));
            }
            type = field.getType().getComponentType().toString()
                    + "[" + (arrayLength >= 0? arrayLength : "") + "]";
            
        } else {
            type = field.getType().toString();
        }

        // get name
        String name = field.getName();

        // prepare return array
        JComponent[] result;
        if (isPublic) {
            if (isArray) {
                result = new JComponent[3 + arrayLength];
            } else {
                result = new JComponent[4];
            }
        } else {
            result = new JComponent[3];
        }

        // fill names etc.
        result[0] = new JLabel(accessModifer);
        result[1] = new JLabel(type);
        result[2] = new JLabel(name);

        // fill array
        if (isPublic && isArray) {

            for (int i = 3; i < result.length; i++) {
                Object o = field.get(object);

                if (o instanceof boolean[]) {
                    result[i] = new JButton("" + ((boolean[]) o)[i - 3]);

                } else if (o instanceof byte[]) {
                    result[i] = new JButton("" + ((byte[]) o)[i - 3]);

                } else if (o instanceof short[]) {
                    result[i] = new JButton("" + ((short[]) o)[i - 3]);

                } else if (o instanceof int[]) {
                    result[i] = new JButton("" + ((int[]) o)[i - 3]);

                } else if (o instanceof long[]) {
                    result[i] = new JButton("" + ((long[]) o)[i - 3]);

                } else if (o instanceof float[]) {
                    result[i] = new JButton("" + ((float[]) o)[i - 3]);

                } else if (o instanceof double[]) {
                    result[i] = new JButton("" + ((double[]) o)[i - 3]);

                } else if (o instanceof char[]) {
                    result[i] = new JButton("" + ((char[]) o)[i - 3]);

                } else if (o instanceof Object[]) {
                    result[i] = new JButton("" + ((Object[]) o)[i - 3]);

                } else {
                    throw new ShouldNeverHappenException();
                }
                
                final int fi = i;
                final JButton fButton = (JButton) result[i];

                fButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String value = JOptionPane.showInputDialog(null);
                        boolean ok = true;
                        try {
                            setArrayValue(field.get(object), fi - 3, value);
                        } catch (Exception ex) {
                            ok = false;
                            JOptionPane.showMessageDialog(null, ex, null,
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        if (ok) {
                            fButton.setText(value);
                        }
                    }
                });

            }

        // fill single value
        } else if (isPublic) {
            result[3] = new JButton(field.get(object).toString());
            final JButton fButton = (JButton) result[3];

            fButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String value = JOptionPane.showInputDialog(null);
                    boolean ok = true;
                    try {
                        setValue(field, object, value);
                    } catch (Exception ex) {
                        ok = false;
                        JOptionPane.showMessageDialog(null, ex, null,
                                JOptionPane.ERROR_MESSAGE);
                    }
                    if (ok) {
                        fButton.setText(value);
                    }
                }
            });
        }

        return result;
    }
    
    /**
     * Modifies object's field to value.
     * @param field field to be modified
     * @param object object whose field is going to change
     * @param value new value
     * @throws IllegalArgumentException if value.length == 0
     *         or field is a char and value.length > 1
     * @throws IllegalAccessException 
     */
    private void setValue(Field field, Object object, String value)
                       throws IllegalArgumentException, IllegalAccessException {
        
        if (value.length() == 0) {
            throw new IllegalArgumentException("No input");
        }
        
        if (field.getType() == boolean.class) {
            field.setBoolean(object, Boolean.parseBoolean(value));

        } else if (field.getType() == byte.class) {
            field.setByte(object, Byte.parseByte(value));

        } else if (field.getType() == short.class) {
            field.setShort(object, Short.parseShort(value));

        } else if (field.getType() == int.class) {
            field.setInt(object, Integer.parseInt(value));

        } else if (field.getType() == long.class) {
            field.setLong(object, Long.parseLong(value));

        } else if (field.getType() == float.class) {
            field.setFloat(object, Float.parseFloat(value));

        } else if (field.getType() == double.class) {
            field.setDouble(object, Double.parseDouble(value));

        } else if (field.getType() == char.class) {
            if (value.length() > 1) {
                throw new IllegalArgumentException("String too long");
            }
            field.setChar(object, value.charAt(0));

        } else if (field.getType() == String.class) {
            field.set(object, value);

        } else {
            throw new UnsupportedOperationException("Converting String to"
                    + "non-primitive type not supported");
            
        }
    }
    
    private void setArrayValue(Object object, int index, String value) {
        if (value.length() == 0) {
            throw new IllegalArgumentException("No input");
        }
        
        if (object instanceof boolean[]) {
            ((boolean[]) object)[index] = Boolean.parseBoolean(value);

        } else if (object instanceof byte[]) {
            ((byte[]) object)[index] = Byte.parseByte(value);

        } else if (object instanceof short[]) {
            ((short[]) object)[index] = Short.parseShort(value);

        } else if (object instanceof int[]) {
            ((int[]) object)[index] = Integer.parseInt(value);

        } else if (object instanceof long[]) {
            ((long[]) object)[index] = Long.parseLong(value);

        } else if (object instanceof float[]) {
            ((float[]) object)[index] = Float.parseFloat(value);

        } else if (object instanceof double[]) {
            ((double[]) object)[index] = Double.parseDouble(value);

        } else if (object instanceof char[]) {
            if (value.length() > 1) {
                throw new IllegalArgumentException("String too long");
            }
            ((char[]) object)[index] = value.charAt(0);

        } else if (object instanceof String[]) {
            ((String[]) object)[index] = value;

        } else if (object instanceof Object[]) {
            throw new UnsupportedOperationException("Converting String to"
                    + "non-primitive type not supported");

        } else {
            throw new IllegalArgumentException("Not an array");
            
        }
    }

    private int getArrayLength(Object object) {
        if (object instanceof boolean[]) {
            return ((boolean[]) object).length;

        } else if (object instanceof byte[]) {
            return ((byte[]) object).length;

        } else if (object instanceof short[]) {
            return ((short[]) object).length;

        } else if (object instanceof int[]) {
            return ((int[]) object).length;

        } else if (object instanceof long[]) {
            return ((long[]) object).length;

        } else if (object instanceof float[]) {
            return ((float[]) object).length;

        } else if (object instanceof double[]) {
            return ((double[]) object).length;

        } else if (object instanceof char[]) {
            return ((char[]) object).length;

        } else if (object instanceof Object[]) {
            return ((Object[]) object).length;

        } else {
            throw new IllegalArgumentException("Not an array");
            
        }
    }

    public void update() {
        setObject(object);
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Component c : getComponents()) {
            c.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }
    
}