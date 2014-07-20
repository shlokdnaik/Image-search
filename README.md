Image-search
============
1) Introduction 

The objective of this project is to find a query image (source image ) in a target image. The query image is be fairly prominent in the search image, having similar colors though the shape and may vary due to the perspective. For testing purpose target images with different level of difficulty are given. 

2) Implementation 

We have implemented this project in Java. In case of the query image varying in size, color and orientation, a scale- or rotation-invariant approach would be appropriate. Various methods such as finding similar texture, feature and shape if the source image in the target image can be used. 

Finding the Color Similarity 

One way of determining the difference between two RGB values is to use the euclidean distance: sqrt( (r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2 ) There are different colour spaces than RGB that can be used. This method is useful when the sub-image is most likely near identical (instead of just visually similar), If ARGB colour space exists, we do not want semi-transparent pixels to influence our results as much, we can use: a1 * a2 * sqrt( (r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2 ) which will give a smaller value if the colours have transparency (assuming a1 and a2 are between 0 and 1). 


Comparing images 

To compare equally-sized images we can sum the difference between their individual pixels. This sum is then a measure of the difference and we can search for the region in the image with the lowest difference measure. We can normalize the difference measure to lie between 0 and 1 by dividing it by the size of the sub-image and the maximum possible RGB difference (sqrt(3) with the euclidean distance and RGB values from 0 to 1). Zero would then be an identical match and anything close to one would be as different as possible

Steps for implementation: 

The target and source image rgb values are matched by calculating the difference in the pixel values. The lowest value of the Euclidean Difference, gives the most appropriate matching positions and the coordinates in the target image where the match has been found. Based on the retrieved coordinates a red box is drawn to show the matching area.
