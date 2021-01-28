    function setOccupancy(zoneNum, isOccupied){
        let queryString = "/zonedata/occupancy/"+ zoneNum + "/" + isOccupied;
        console.log(queryString);
        $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(data){
                console.log("Zone "+zoneNum+" isOccupied now "+ isOccupied);
                getZoneData(zoneNum);
            },
            error: function(){console.log("Error failed to change occupancy");}
         });
    }

    function checkTemp(temp){
        return (temp == -999) ? " -- ":temp;
    }

    function getOnOff(boolData){
        return (boolData ? 'On' : 'Off');
    }

    function getOpenClosed(boolData){
        return (boolData ? 'Open' : 'Closed');
    }         

         function getZoneData(zoneNum){
         console.log(zoneNum);
             let queryString = '/zonedata/zonesettings/'+zoneNum;
             console.log(queryString);
             $.ajax({
                 type: 'GET',
                 dataType: 'json',
                 url: queryString,
                 success: function(data){
                     document.getElementById("#ZONE"+zoneNum+"ALIAS").innerHTML = data.alias;
                     document.getElementById("#ZONE"+zoneNum+"USINGTEMP").innerHTML = checkTemp(data.PROCESSTEMP);
                     document.getElementById("#ZONE"+zoneNum+"SETPOINT").innerHTML = checkTemp(data.PROCESSSETPOINT);
                     document.getElementById("#ZONE"+zoneNum+"MODE").innerHTML = data.mode;
                     document.getElementById("#ZONE"+zoneNum+"FAN").innerHTML = (data.fansRequested == 'true') ? "ON":"AUTO";
                     document.getElementById("#ZONE"+zoneNum+"OCCUPANCY").innerHTML = (data.isOccupied == 'true') ? 'OCCUPIED' : 'UNOCCUPIED';
                 },
                 error: function(data){
                     console.log("Error Failed To Retrieve settings Data"+data);

                 }
             });
         }
      
    function getZoneDataForIndex(zoneNum){
        let queryString = '/zonedata/zonesettings/'+zoneNum;
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: queryString,
            success: function(data){
                document.getElementById("#ZONE"+zoneNum+"ALIAS").innerHTML = data.alias;
                document.getElementById("#ZONE"+zoneNum+"TEMP1").innerHTML = checkTemp(data.PRIMARYTEMP);
                document.getElementById("#ZONE"+zoneNum+"TEMP2").innerHTML = checkTemp(data.SECONDARYTEMP);
                document.getElementById("#ZONE"+zoneNum+"USINGTEMP").innerHTML = checkTemp(data.PROCESSTEMP);
                document.getElementById("#ZONE"+zoneNum+"SETPOINT").innerHTML = checkTemp(data.PROCESSSETPOINT);
                document.getElementById("#ZONE"+zoneNum+"MODE").innerHTML = data.mode;
                document.getElementById("#ZONE"+zoneNum+"FAN").innerHTML = (data.fansRequested == 'true') ? "ON":"AUTO";
                document.getElementById("#ZONE"+zoneNum+"OCCUPANCY").innerHTML = (data.isOccupied == 'true') ? 'OCCUPIED' : 'UNOCCUPIED';
            },
            error: function(data){
                console.log("Error Failed To Retrieve settings Data"+data);
            }
         });
    }

    function getOutputs(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/outputs',
            success: function(data){
                document.getElementById("#W1_OUTPUT").innerHTML = getOnOff(data[0]);
				document.getElementById("#W2_OUTPUT").innerHTML = getOnOff(data[1]);
				document.getElementById("#Y1_OUTPUT").innerHTML = getOnOff(data[2]);
				document.getElementById("#Y2_OUTPUT").innerHTML = getOnOff(data[3]);
				document.getElementById("#G_OUTPUT").innerHTML  = getOnOff(data[4]);

				document.getElementById("#DAMPERZ1_OUTPUT").innerHTML = getOpenClosed(data[5]);
				document.getElementById("#DAMPERZ2_OUTPUT").innerHTML = getOpenClosed(data[6]);
				document.getElementById("#DAMPERZ3_OUTPUT").innerHTML = getOpenClosed(data[7]);
            },
            error: function(){
               console.log("Error Failed To Retrieve Outputs Data");
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
                getZoneData(zoneNum);
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
                getZoneData(zoneNum);
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
                     getZoneData(zoneNum);
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
                     getZoneData(zoneNum);
                 },
                 error: function(){alert("Error failed to change fan setting");}
             });
         }
