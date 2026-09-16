package pulsetest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    public void register(Module module) { modules.add(module); }
    public List<Module> all() { return Collections.unmodifiableList(modules); }
    public List<Module> category(Category category) { return modules.stream().filter(m -> m.getCategory() == category).toList(); }
    public void tick() { for (Module m : modules) if (m.isEnabled()) m.tick(); }
    public void disableAll() { for (Module m : modules) m.setEnabled(false); }
}
