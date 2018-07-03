# Phasetime

## What we'll build
One of the crucial elements of pursuing Photography is understanding to use 
Earth's natural light sources ( *Sun and Moon* ) to our advantage. A trick to naturally take better photographs is to shoot in the [Golden Hour](https://en.wikipedia.org/wiki/Golden_hour_(photography)). As the name suggests this begins approximately an hour before sunset. 

In order to assist Photographers with planning of their photoshoots, we'll build an Application that provides Rising & Setting time of Sun and the Moon. **Rising & Setting time will be referred to as _Phasetime_ hereafter**. 

**Phasetime** can be calculated using this [Algorithm](https://web.archive.org/web/20161202180207/http://williams.best.vwh.net/sunrise_sunset_algorithm.htm). Implementation requires a date and location ( _longitude and lattitude_ ) as an input. User can provide the desired location using a Search Bar, move the Red pin or the Application will use current GPS location as default. For quick access, Application will be able to show past persisted locations by the user.
