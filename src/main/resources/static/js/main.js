$('document').ready(function(){
    $("#chooseFile").bind('change',function () {
        var filename = $("#chooseFile").val();
        if (/^\s*$/.test(filename)) {
            $(".file-upload").removeClass('active');
            $("#btnSubmit").removeClass('active');
            $("#btnSubmit").removeClass('warning');
            $("#noFile").text("No file chosen...");
            $("#fileText").val("");
        }
        else {
            $(".file-upload").addClass('active');
            $("#noFile").text(filename.replace("C:\\fakepath\\", ""));
        }
    });

    $("#btnSubmit").click(function (event) {
        //stop submit the form, we will post it manually.
        event.preventDefault();
        fire_ajax_submit();
    });

});



function fire_ajax_submit(){
    //Get Form
    var form = $("#fileUploadForm")[0];
    console.log("Form: "+form);

    var data = new FormData(form);
    console.log("Data: "+data);

    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type:"POST",
        enctype : 'multipart/form-data',
        url : "/api/upload/multi",
        data: data,
        processData: false, //Prevent jQuery from automatically transofming the data into a to a query string
        contentType: false,
        cache:false,
        timeout: 60000,
        success: function(data){
            if(data=="please select a file!"){
                $("#btnSubmit").addClass('warning');
            }
            else{
                $("#btnSubmit").removeClass('warning');
                $("#btnSubmit").addClass('active');
            }
            $("#btnSubmit").prop("disabled",false);
            $("#fileText").val(data);
        },
        error: function(e){
            $("#result").text(e.resonseText);
            console.log("ERROR : ",e);
            $("#btnSubmit").prop("disabled", false);
        }
    });

}