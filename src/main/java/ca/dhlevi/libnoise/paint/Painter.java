package ca.dhlevi.libnoise.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import ca.dhlevi.libnoise.Point;
import ca.dhlevi.libnoise.Utilities;
import ca.dhlevi.libnoise.spatial.Envelope;
import ca.dhlevi.libnoise.spatial.SpatialUtilities;
public class Painter
{
    public static boolean paintHeightMap(double[][] data, String path) throws IOException
    {
        int width = data.length;
        int height = data[0].length;

        BufferedImage mapImage = createMapImageBuffer(width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double noiseValue = data[x][y];
                if (noiseValue > 1)
                    noiseValue = 1;
                if (noiseValue < 0)
                    noiseValue = 0;

                int rgb = (int) Math.round(255 * noiseValue);

                Color c = new Color(rgb, rgb, rgb, 255);

                mapImage.setRGB(x, y, c.getRGB());
            }
        }

        return saveBufferedImage(mapImage, path, "heightmap");
    }

    public static boolean paintRegionMap(int[][] data, String path, int seed) throws IOException
    {
        int width = data.length;
        int height = data[0].length;

        BufferedImage mapImage = createMapImageBuffer(width, height);

        HashMap<Integer, Color> regionColors = new HashMap<Integer, Color>();
        Random rand = new Random(seed);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int region = data[x][y];
                Color regionColor = null;

                if (region == 0)
                {
                    regionColor = Color.BLACK;
                } 
                else if (regionColors.containsKey(region))
                {
                    regionColor = regionColors.get(region);
                } 
                else
                {
                    boolean uniqueColor = false;
                    while (!uniqueColor)
                    {
                        regionColor = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                        uniqueColor = !regionColors.containsValue(regionColor);
                    }

                    regionColors.put(region, regionColor);
                }

                mapImage.setRGB(x, y, regionColor.getRGB());
            }
        }

        return saveBufferedImage(mapImage, path, "regions");
    }

    public static boolean paintTerrainMap(double[][] data, int[][] rivers, double seaLevel, String path, boolean hillshade, boolean shadeWater) throws IOException
    {
        int width = data.length;
        int height = data[0].length;

        BufferedImage mapImage = createMapImageBuffer(width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double noiseValue = data[x][y];
                if (noiseValue > 1)
                    noiseValue = 1;
                if (noiseValue < 0)
                    noiseValue = 0;

                // terrain painter
                if (noiseValue < seaLevel || rivers[x][y] != 0)
                {
                    Color c = new Color(153, 217, 242, 255);
                    if (shadeWater)
                    {
                        Color shade = Hillshader.shadePixel(data, width, height, x, y, 0).getRGB();
                        c = PainterUtilities.blend(c, shade);
                    }

                    mapImage.setRGB(x, y, c.getRGB());
                }
                else
                {
                    Color c = PainterUtilities.gradient(new Color(0, 102, 0, 255), new Color(220, 220, 220, 255), noiseValue);
                    if (hillshade)
                    {
                        Color shade = Hillshader.shadePixel(data, width, height, x, y, 0).getRGB();
                        c = PainterUtilities.blend(c, shade);
                    }

                    mapImage.setRGB(x, y, c.getRGB());
                }
            }
        }

        return saveBufferedImage(mapImage, path, "terrain");
    }

    public static boolean paintBiomeMap(double[][] data, int[][] rivers, int[][] biomes, double seaLevel, String path, boolean hillshade, boolean shadeWater) throws IOException
    {
        int width = data.length;
        int height = data[0].length;

        BufferedImage mapImage = createMapImageBuffer(width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double noiseValue = data[x][y];
                if (noiseValue > 1)
                    noiseValue = 1;
                if (noiseValue < 0)
                    noiseValue = 0;

                // terrain painter
                if ((noiseValue < seaLevel || rivers[x][y] != 0) && biomes[x][y] != 1)
                {
                    Color c = new Color(153, 217, 242, 255);
                    if (shadeWater)
                    {
                        Color shade = Hillshader.shadePixel(data, width, height, x, y, 0).getRGB();
                        c = PainterUtilities.blend(c, shade);
                    }

                    mapImage.setRGB(x, y, c.getRGB());
                } 
                else
                {
                    Color c = Color.white;

                    if (biomes[x][y] == 1)
                        c = new Color(225, 225, 225, 255);
                    if (biomes[x][y] == 2)
                        c = new Color(156, 153, 160, 255);
                    if (biomes[x][y] == 3)
                        c = new Color(53, 76, 55, 255);

                    if (biomes[x][y] == 4)
                        c = new Color(92, 103, 84, 255);
                    if (biomes[x][y] == 5)
                        c = new Color(15, 154, 58, 255);
                    if (biomes[x][y] == 6)
                        c = new Color(2, 32, 21, 255);

                    if (biomes[x][y] == 7)
                        c = new Color(215, 187, 165, 255);
                    if (biomes[x][y] == 8)
                        c = new Color(179, 168, 148, 255);
                    if (biomes[x][y] == 9)
                        c = new Color(108, 107, 89, 255);

                    if (hillshade)
                    {
                        Color shade = Hillshader.shadePixel(data, width, height, x, y, 0).getRGB();
                        c = PainterUtilities.blend(c, shade);
                    }

                    mapImage.setRGB(x, y, c.getRGB());
                }
            }
        }

        return saveBufferedImage(mapImage, path, "biome");
    }

    public static boolean paintMoistureLevels(double[][] data, String path) throws IOException
    {
        int width = data.length;
        int height = data[0].length;

        BufferedImage mapImage = createMapImageBuffer(width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double noiseValue = data[x][y];
                if (noiseValue > 1)
                    noiseValue = 1;
                if (noiseValue < 0)
                    noiseValue = 0;

                int rgb = (int) Math.round(255 * noiseValue);

                Color c = new Color(0, 0, rgb, 255);

                mapImage.setRGB(x, y, c.getRGB());
            }
        }

        return saveBufferedImage(mapImage, path, "moisture");
    }

    public static boolean paintTempuratureBands(double[][] data, double tempRange, String path, Envelope bbox) throws IOException
    {
        int width = data.length;
        int height = data[0].length;

        BufferedImage mapImage = createMapImageBuffer(width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double lat = SpatialUtilities.pixelsToLatLong(new Point(x, y), width, height, bbox).getY();
                if (lat < 0)
                    lat = lat * -1;
                double temp = (tempRange * ((90 - lat) / 90)) * (1.0 - data[x][y]);

                int rgb = (int) Math.round(255 * (temp / tempRange));

                if(rgb < 0) rgb = 0;
                if(rgb > 255) rgb = 255;
                
                Color c = new Color(0, 0, 0, 255);

                if (temp < 10)
                {
                    c = new Color(0, 0, rgb, 255);
                } 
                else if (temp >= 10 && temp < 20)
                {
                    c = new Color(0, rgb, 0, 255);
                } 
                else if (temp >= 20)
                {
                    c = new Color(rgb, 0, 0, 255);
                }

                mapImage.setRGB(x, y, c.getRGB());
            }
        }

        return saveBufferedImage(mapImage, path, "tempuratures");
    }

    private static BufferedImage createMapImageBuffer(int width, int height)
    {
        BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) mapImage.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        return mapImage;
    }

    private static boolean saveBufferedImage(BufferedImage image, String path, String filename) throws IOException
    {
        image.flush();

        if (!path.endsWith(File.separator))
            path += File.separator;

        File outputfile = new File(path + filename + ".png");
        outputfile.createNewFile();
        boolean success = ImageIO.write(image, "png", outputfile);

        return success;
    }
}
