package com.luncert.robotcontraption.compat.computercraft;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.luncert.robotcontraption.compat.aircraft.IAircraftComponent;
import com.luncert.robotcontraption.compat.create.EAircraftMovementMode;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.util.Common;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class AircraftStationPeripheral implements IPeripheral {

    protected String type;
    protected AircraftStationTileEntity tileEntity;

    protected final List<IComputerAccess> connected = new ArrayList<>();

    public AircraftStationPeripheral(String type, AircraftStationTileEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connected;
    }

    @Override
    public Object getTarget() {
        return tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @Override
    public void attach(IComputerAccess computer) {
        connected.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        connected.remove(computer);
    }

    // api

    @LuaFunction(mainThread = true)
    public final void assemble(String rotationMode) throws LuaException {
        EAircraftMovementMode mode;
        try {
            mode = EAircraftMovementMode.valueOf(rotationMode.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new LuaException("Invalid argument, must be one of " + Arrays.toString(EAircraftMovementMode.values()));
        }

        try {
            tileEntity.assemble(mode);
        } catch (AircraftAssemblyException e) {
            e.printStackTrace();
            throw new LuaException("failed to assemble structure: " + e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final void dissemble() throws LuaException {
        try {
            tileEntity.dissemble();
        } catch (AircraftAssemblyException e) {
            e.printStackTrace();
            throw new LuaException("failed to dissemble structure: " + e.getMessage());
        }
    }

    @LuaFunction
    public List<String> getComponents() {
        Map<String, List<IAircraftComponent>> components = tileEntity.getComponents();
        List<String> result = new ArrayList<>(components.size());
        for (List<IAircraftComponent> value : components.values()) {
            for (int i = 0; i < value.size(); i++) {
                IAircraftComponent c = value.get(i);
                result.add(c.getComponentType().getName() + "-" + i);
            }
        }
        return result;
    }

    @LuaFunction
    public Map<String, ILuaFunction> getComponent(String componentName) throws LuaException {
        Pair<String, Integer> name = BaseAircraftComponent.parseName(componentName);

        List<IAircraftComponent> components = tileEntity.getComponents().get(name.getKey());
        if (components != null && components.size() > name.getValue()) {
            return getLuaFunctions(components.get(name.getValue()));
        }

        return Collections.emptyMap();
    }

    private Map<String, ILuaFunction> getLuaFunctions(IAircraftComponent c) {
        Map<String, ILuaFunction> functions = new HashMap<>();
        for (Method method : c.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(LuaFunction.class)) {
                String name = method.getDeclaringClass().getName() + "." + method.getName();
                int modifiers = method.getModifiers();

                if(!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                    RobotContraption.LOGGER.warn("Lua Method {} should be final.", name);
                }

                if(!Modifier.isPublic(modifiers)) {
                    RobotContraption.LOGGER.error( "Lua Method {} should be a public method.", name );
                    return Collections.emptyMap();
                }

                functions.put(method.getName(), generate(c, method));
            }
        }

        return functions;
    }

    private ILuaFunction generate(IAircraftComponent c, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return arguments -> {
            Object[] args = parseArguments(arguments, parameterTypes);
            try {
                Object result = method.invoke(c, args);
                if (result instanceof MethodResult r) {
                    return r;
                }
                return MethodResult.of(result);
            } catch (Exception e) {
                RobotContraption.LOGGER.error("failed to call component api", e);
                throw new LuaException(Common.findCauseUsingPlainJava(e).getMessage());
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Object[] parseArguments(IArguments arguments, Class<?>[] parameterTypes) throws LuaException {
        if (arguments.count() != parameterTypes.length) {
            throw new LuaException(parameterTypes.length + " arguments expected, received " + arguments.count());
        }

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Object value;
            if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                value = arguments.getBoolean(i);
            } else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
                value = arguments.getBytes(i);
            } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                value = arguments.getInt(i);
            } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
                value = arguments.getFiniteDouble(i);
            } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                value = arguments.getLong(i);
            } else if (Map.class.isAssignableFrom(type)) {
                value = arguments.getTable(i);
            } else if (String.class.isAssignableFrom(type)) {
                value = arguments.getString(i);
            } else if (Enum.class.isAssignableFrom(type)) {
                value = arguments.getEnum(i, (Class<? extends Enum>) type);
            } else {
                throw new LuaException("invalid argument " + i + ", " + type + " expected");
            }
            args[i] = value;
        }
        return args;
    }
}
