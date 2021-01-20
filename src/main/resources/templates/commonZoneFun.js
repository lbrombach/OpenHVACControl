             function setOccupancy(zoneNum, isOccupied){
                 let queryString = "/zonedata/occupancy/"+ zoneNum + "/" + isOccupied;
                 console.log(queryString);
                 $.ajax({
                 type: 'PUT',
                 url: queryString,
                 success: function(data){
                     console.log("Zone "+zoneNum+" isOccupied now "+ isOccupied);
                     getSettings(zoneNum);
                 },
                 error: function(){console.log("Error failed to change occupancy");}
             });
         }

         function checkTemp(temp){
             return (temp == -999) ? " -- ":temp;
         }

         function getSettings(zoneNum){
         console.log(zoneNum);
             let queryString = '/zonedata/zonesettings/'+zoneNum;
             console.log(queryString);
             $.ajax({
                 type: 'GET',
                 dataType: 'json',
                 url: queryString,
                 success: function(data){
                     document.getElementById("#ZONEALIAS").innerHTML = data.alias;
                     document.getElementById("#ZONEUSINGTEMP").innerHTML = checkTemp(data.PROCESSTEMP);
                     document.getElementById("#ZONESETPOINT").innerHTML = checkTemp(data.PROCESSSETPOINT);
                     document.getElementById("#ZONEMODE").innerHTML = data.mode;
                     document.getElementById("#ZONEFAN").innerHTML = (data.fansRequested == 'true') ? "ON":"AUTO";
                     document.getElementById("#OCCUPANCY").innerHTML = (data.isOccupied == 'true') ? 'OCCUPIED' : 'UNOCCUPIED';
                 },
                 error: function(data){
                     console.log("Error Failed To Retrieve settings Data"+data);

                 }
             });
         }

    function increaseSP(zoneNum){
            let queryString = '/setpoints/increase/' + zoneNum;
            console.log(queryString);
            $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(data){
                console.log("Setpoint " + zoneNum + " increased");
                getSettings(zoneNum);
            },
            error: function(){alert("Error failed to change setpoint");}
        });
    }

    function decreaseSP(zoneNum){
            let queryString = '/setpoints/decrease/' + zoneNum;
            $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(data){
                console.log("Setpoint " + zoneNum + " decreased");
                getSettings(zoneNum);
            },
            error: function(){console.log("Error failed to change setpoint");}
        });
    }


             function setMode(zoneNum, newMode){
             console.log("zone " + zoneNum + " to " + newMode);
             let queryString = '/zonedata/modes/' + zoneNum + '/' + newMode;
             console.log(queryString);
             $.ajax({
                 type: 'PUT',
                 url: queryString,
                 success: function(){
                     console.log("Mode " + zoneNum + " changed to " + newMode);
                     getSettings(zoneNum);
                 },
                 error: function(){console.log("Error failed to change mode");}
             });
         }

             function toggleFanOnAuto(zoneNum){
             console.log("this toggle fan? " + zoneNum);
                 let queryString = '/zonedata/fans/' + zoneNum;
                 $.ajax({
                 type: 'PUT',
                 url: queryString,
                 success: function(){
                     getSettings(zoneNum);
                 },
                 error: function(){alert("Error failed to change fan setting");}
             });
         }