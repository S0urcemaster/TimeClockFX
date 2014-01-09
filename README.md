# TimeClockFX

A simple time clock app with scalaFX.

Features:
- Tracks come/go in monthly file
- Displays times per month

[Download latest stable build TimeClockFX.zip (file-upload.net page)](http://www.file-upload.net/download-8497539/TimeClockFX.zip.html)

## Timeline
- 09.01.14
	- Cosmetics
	
- 08.01.14
	- RC1 released
	- Fixed Ordering

- 07.01.14
	- Month duration fix
	- Sorting fix

- 06.01.14 
	- Added periods display

- Beta release 03.01.14
	- missing periods
	
- Alpha Buglist 03.01.14
  - Adding to List is reverse
  - missing validation for forgotTextField input
  - no 24h times
  - missing enter-to-ok in dialog
  - file not closed
  - forgotCome range is not limited to times after the last time
  
  Optional:
  ? verbose input error messages
  
- Created 21.12.13

## Roadmap
### Required
- Tests
	- Timing
	  - Month change
  - Linux
  - Resizing
- Cosmetics
- Simple file backup

### Issues
- Month/Year change while running
- Line Breaks Windows

### Optional
- German translation
- Configuration
	- Single-/ double Button design
	- Different styles
- Better dialog

## Requirements Specification

- Come/Go -function
- Forgot for come/go
- Data storage in a file
- Displaying of the last 10 days
  - 1 Come/Go per row
  - colored per day in a 2-day cycle
- App resizes in windows desktop DPI scale
