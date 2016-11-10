# Versatile Analysis of Body Temperature

## Description

This project is an application designed to read, analyse and summaries timeseries data from temperature loggers placed in a variety of animals for an extended period of time. The software runs a periodogram and cosinor fit on a selected subset of the data producing a report detailing the relevant periods, MESOR, amplitude, acrophase, Mean Square Residual and Outliers. This analysis is useful for analysing the health of the animals being measured giving indications of changes in their wellbeing.   

This software was created by Viktor Fidanovski, Xue Yu, Alex Emery and Lewis Tolonen for clients Dominique Blache and Shane Maloney.   

## Source Directory

All java source files needed for compilation are included in the code directory including the JFreeChart library which is included in
the "Libraries" folder.   
Note: This project uses (within the permissions of the MIT License) the Fast Fourier Transform implementation created for Project Nayuki
(https://www.nayuki.io/page/free-small-fft-in-multiple-languages). See FFT.java

## Build

Using Eclipse or similar IDE:   
1. Import all the source code.   
2. Link project to require libraries in Libraries folder.   
3. Export as Runnable Jar file, selecting window.java as the location of main class and including the libraries in that jar.   

## Install

There is no install process, once the project is exported as a runnable jar it can be run and opened without anything further.   

## Running

1. Double click created .jar file (or use provided "VABT.jar").   
2. Select "file" in top left corner and then "open" from those options.   
3. Navigate to and select an appropriately formatted csv file to load into the program and hit open.   
4. After waiting a few seconds the graph will appear of that data. Refer to "Instructions" on how to navigate in more detail.   
5. Enter start and end dates for the analysis (making sure to hit enter after entering anything into a box) and press the analyse button.   
6. Modify the outlier tolerance if required.   
7. Save the results of the analysis to "NAME_OF_DATASET - RESULTS.csv"   
8. Press reset to do another analysis or another file can be opened on top of the present file.   

## User Documentation

### Periodogram  
When the Analyse button is pressed the system first performs a Periodogram on the data in the selected window. This is implemented through a Fast Fourier Transform, getting the relative prevalence of different frequencies in the data. These frequencies are then converted to periods and the most significant is used for the analysis.   
### Cosinor Analysis   
When the Analyse button is pressed, after performing the Periodogram a Cosinor fit is run on the data which involves fitting a cosine curve of the calculated period to the data. This involves solving a system of equations to calculate the MESOR, Amplitude and Phase that minimises the sum of the square of the residuals between the fitted values and the actual values.   
### Outlier   
Outliers are calculated using the amplitude of the Cosinor fit and the current fit value. The Outlier Tolerance box defines how many multiples of the amplitude away from the current fit value the data point must be to be classified as an outlier. In other words:   Outlier Threshold = Current Fit Value ± Outlier Tolerance * Amplitude of Fit   
### Mean Squares Residual   
After fitting the curve the Mean Square Residual (MSR) is calculated. This is simply the average of the square of the difference between the fitted value and actual value in the range of the analysis.   


## License

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.   
