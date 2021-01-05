    function showAliases(data){
        document.getElementById("#ZONE1ALIAS").innerHTML = data[0];
        document.getElementById("#ZONE2ALIAS").innerHTML = data[1];
        document.getElementById("#ZONE3ALIAS").innerHTML = data[2];
    }

    function showModes(data){
        document.getElementById("#ZONE1MODE").innerHTML = data[0];
        document.getElementById("#ZONE2MODE").innerHTML = data[1];
        document.getElementById("#ZONE3MODE").innerHTML = data[2];
    }

    function showFans(data){
        document.getElementById("#ZONE1FAN").innerHTML = (data[0] == true) ? "ON":"AUTO";
        document.getElementById("#ZONE2FAN").innerHTML = (data[1] == true) ? "ON":"AUTO";
        document.getElementById("#ZONE3FAN").innerHTML = (data[2] == true) ? "ON":"AUTO";
    }



    function showTemps(data){
        document.getElementById("#ZONE1TEMP1").innerHTML = (data[0] == -999) ? " -- ":data[0];
        document.getElementById("#ZONE2TEMP1").innerHTML = (data[1] == -999) ? " -- ":data[1];
        document.getElementById("#ZONE3TEMP1").innerHTML = (data[2] == -999) ? " -- ":data[2];
        document.getElementById("#ZONE1TEMP2").innerHTML = (data[3] == -999) ? " -- ":data[3];
        document.getElementById("#ZONE2TEMP2").innerHTML = (data[4] == -999) ? " -- ":data[4];
        document.getElementById("#ZONE3TEMP2").innerHTML = (data[5] == -999) ? " -- ":data[5];
        document.getElementById("#ZONE1USINGTEMP").innerHTML = data[6];
        document.getElementById("#ZONE2USINGTEMP").innerHTML = data[7];
        document.getElementById("#ZONE3USINGTEMP").innerHTML = data[8];
    }

    function showSetpoints(data){
        document.getElementById("#ZONE1SETPOINT").innerHTML = data[0];
        document.getElementById("#ZONE2SETPOINT").innerHTML = data[1];
        document.getElementById("#ZONE3SETPOINT").innerHTML = data[2];
    }


    function showOutputs(data){
        document.getElementById("#W1_OUTPUT").innerHTML = getOnOff(data[0]);
        document.getElementById("#W2_OUTPUT").innerHTML = getOnOff(data[1]);
        document.getElementById("#Y1_OUTPUT").innerHTML = getOnOff(data[2]);
        document.getElementById("#Y2_OUTPUT").innerHTML = getOnOff(data[3]);
        document.getElementById("#G_OUTPUT").innerHTML  = getOnOff(data[4]);

        document.getElementById("#DAMPERZ1_OUTPUT").innerHTML = getOpenClosed(data[5]);
        document.getElementById("#DAMPERZ2_OUTPUT").innerHTML = getOpenClosed(data[6]);
        document.getElementById("#DAMPERZ3_OUTPUT").innerHTML = getOpenClosed(data[7]);
    }

    function getOnOff(boolData){
        return (boolData ? 'On' : 'Off');
    }

    function getOpenClosed(boolData){
        return (boolData ? 'Open' : 'Closed');
    }


    function getAliases(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/zonedata/aliases',
            success: function(data){
                showAliases(data);
            },
            error: function(){
                console.log("Error Failed To Retrieve Alias Data");
            }
        });
    }

    function getTemps(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/temps',
            success: function(data){
                showTemps(data);
            },
            error: function(){alert("Error Failed To Retrieve Temperature Data");}
        });
    }

    function getSetpoints(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/setpoints',
            success: function(data){
                showSetpoints(data);
            },
            error: function(){alert("Error Failed To Retrieve Setpoint Data");}
        });
    }

    function getOutputs(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/outputs',
            success: function(data){
                showOutputs(data);
            },
            error: function(){
               console.log("Error Failed To Retrieve Outputs Data");
            }
        });
    }

    function getModes(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/zonedata/modes',
            success: function(data){
                showModes(data);
            },
            error: function(){
                console.log("Error Failed To Retrieve Mode Data");
            }
        });
    }

    function getFanSettings(){
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/zonedata/fans',
            success: function(data){
                showFans(data);
            },
            error: function(){
                console.log("Error Failed To Retrieve Fan Data");
            }
        });
    }

    function setMode(zoneNum, newMode){
        console.log("zone " + zoneNum + " to " + newMode);
        let queryString = '/zonedata/modes/' + zoneNum + '/' + newMode;
        $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(){
                console.log("Mode " + zoneNum + " changed to " + newMode);
                getModes();
            },
            error: function(){alert("Error failed to change mode");}
        });
    }

    function toggleFanOnAuto(zoneNum){
            let queryString = '/zonedata/fans/' + zoneNum;
            $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(){
                getFanSettings();
            },
            error: function(){alert("Error failed to change fan setting");}
        });
    }

    function increaseSP(zoneNum){
            let queryString = '/setpoints/increase/' + zoneNum;
            console.log
            $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(data){
                console.log("Setpoint " + zoneNum + " increased");
                updateSetpoint(zoneNum, data);
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
                updateSetpoint(zoneNum, data);
            },
            error: function(){alert("Error failed to change setpoint");}
        });
    }

    function updateSetpoint(zoneNum, data){
        if(zoneNum == 1){
            document.getElementById("#ZONE1SETPOINT").innerHTML = data;
        }
        else if (zoneNum == 2){
            document.getElementById("#ZONE2SETPOINT").innerHTML = data;
        }
        else if (zoneNum == 3){
            document.getElementById("#ZONE3SETPOINT").innerHTML = data;
        }
    }

                //this is what works in postman: to delete users from DB:
                //localhost:8080/user?emailAddress=example@wayne.edu

    function submitTask1Name(input){
        let newName = document.getElementById("TASK1NEW_NAME").value;
        //  let queryString = '/setTask1Name?newName=' + newName;
        let queryString = '/setTask1Name/' + newName;
        $.ajax({
            type: 'PUT',
            url: queryString,
            success: function(){
                console.log("Post completed");
              },
            error: function(){alert("Error Failed To Post new Data");}
        });
    }