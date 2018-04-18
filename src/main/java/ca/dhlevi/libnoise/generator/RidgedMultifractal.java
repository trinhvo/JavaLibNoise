package ca.dhlevi.libnoise.generator;

import ca.dhlevi.libnoise.Utilities;

public class RidgedMultifractal extends Generator
{
    private double frequency;
    private double lacunarity;
    private QualityMode quality;
    private int octave;
    private int seed;

    private double[] weights = new double[Utilities.MAX_OCTAVE];

    public RidgedMultifractal()
    {
        this.frequency = 1.0;
        this.lacunarity = 1.0;
        this.octave = 5;
        this.seed = 1;
        this.quality = QualityMode.Medium;

        updateWeights();
    }

    public RidgedMultifractal(double frequency, double lacunarity, int octaves, int seed, QualityMode quality)
    {
        this.frequency = frequency;
        this.lacunarity = lacunarity;
        this.octave = octaves;
        this.seed = seed;
        this.quality = quality;

        updateWeights(); // not needed here?
    }

    private void updateWeights()
    {
        double f = 1.0;

        for (int i = 0; i < Utilities.MAX_OCTAVE; i++)
        {
            weights[i] = Math.pow(f, -1.0);
            f *= lacunarity;
        }
    }

    @Override
    public double getValue()
    {
        return getValue(0, 0, 0, 1);
    }

    @Override
    public double getValue(double x, double y, double z, int scale)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double value = 0.0;
        double weight = 1.0;
        double offset = 1.0; 
        double gain = 2.0;

        for (int i = 0; i < octave + scale; i++)
        {
            double nx = Utilities.makeInt32Range(x);
            double ny = Utilities.makeInt32Range(y);
            double nz = Utilities.makeInt32Range(z);

            long modSeed = (seed + i) & 0x7fffffff;
            double signal = Utilities.gradientCoherentNoise3D(nx, ny, nz, modSeed, quality);

            signal = Math.abs(signal);
            signal = offset - signal;
            signal *= signal;
            signal *= weight;

            weight = signal * gain;
            weight = Utilities.clamp((float) weight);

            value += (signal * weights[i]);

            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }

        return (value * 1.25) - 1.0;
    }

    public double getFrequency()
    {
        return frequency;
    }

    public void setFrequency(double frequency)
    {
        this.frequency = frequency;
    }

    public double getLacunarity()
    {
        return lacunarity;
    }

    public void setLacunarity(double lacunarity)
    {
        this.lacunarity = lacunarity;
        updateWeights();
    }

    public QualityMode getQuality()
    {
        return quality;
    }

    public void setQuality(QualityMode quality)
    {
        this.quality = quality;
    }

    public int getOctave()
    {
        return octave;
    }

    public void setOctave(int octave)
    {
        this.octave = Utilities.clamp(octave, 1, Utilities.MAX_OCTAVE);
    }

    public int getSeed()
    {
        return seed;
    }

    public void setSeed(int seed)
    {
        this.seed = seed;
    }
}
