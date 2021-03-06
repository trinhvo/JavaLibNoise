package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Invert extends Operator
{
    public Invert(Module input)
    {
        this.getModules().add(input);
    }

    @Override
    public double getValue()
    {
        return getValue(0, 0, 0, 1);
    }

    @Override
    public double getValue(double x, double y, double z, int scale)
    {
        return -this.getModules().get(0).getValue(x, y, z, scale);
    }
}
