$('document').ready(function(){

    encrypt = "Encrypt";
    decrypt = "Decrypt";

    $("#error").hide();
    $("#success").hide();

    $("#btnGenerateKeys").click(function (event) {
        event.preventDefault();
        fire_ajax_generate_keys();
    })

    $("#chooseFile").bind('change',function () {
        var filename = $("#chooseFile").val();
        if (/^\s*$/.test(filename)) {
            $("#original").removeClass('active');
            $("#btnSubmit").removeClass('active');
            $("#btnSubmit").removeClass('warning');
            $("#noFile").text("No file chosen...");
            $("#fileTextAsymmetric").val("");
            $("#fileTextSymmetric").val("");
        }
        else {
            $("#original").addClass('active');
            $("#noFile").text(filename.replace("C:\\fakepath\\", ""));
        }
    });

    $("#btnSubmit").click(function (event) {
        //stop submit the form, we will post it manually.
        event.preventDefault();
        fire_ajax_submit();
    });

    $("#btnAsymmetricEncrypt").click(function (event) {
       event.preventDefault();
       $("#btnAsymmetricDecrypt").removeClass("active");
       $("#btnAsymmetricEncrypt").addClass("active");
       fire_ajax_asymmetric(encrypt);


    });

    $("#btnAsymmetricDecrypt").click(function(event){
        event.preventDefault();
        $("#btnAsymmetricEncrypt").removeClass("active");
        $("#btnAsymmetricDecrypt").addClass("active");
        fire_ajax_asymmetric(decrypt);
    });

    $("#btnSymmetricEncrypt").click(function (event) {
       event.preventDefault();
        $("#btnSymmetricDecrypt").removeClass("active");
        $("#btnSymmetricEncrypt").addClass("active");
       fire_ajax_symmetric(encrypt);
    });

    $("#btnSymmetricDecrypt").click(function(event){
       event.preventDefault();
        $("#btnSymmetricEncrypt").removeClass("active");
        $("#btnSymmetricDecrypt").addClass("active");
       fire_ajax_symmetric(decrypt);
    });

    $("#btnCheckSignature").click(function (event) {
        event.preventDefault();
        fire_ajax_compare();
    });


    $("#chooseSecondaryFile").bind('change',function () {
        var filename = $("#chooseSecondaryFile").val();
        if (/^\s*$/.test(filename)) {
            $("#secondary").removeClass('active');
            $("#btnSubmitSecondary").removeClass('active');
            $("#btnSubmitSecondaryn").removeClass('warning');
            $("#noSecondaryFile").text("No file chosen...");

        }
        else {
            $("#secondary").addClass('active');
            $("#noSecondaryFile").text(filename.replace("C:\\fakepath\\", ""));
        }
    });


    $("#chooseSignatureFile").bind('change',function () {
        var filename = $("#chooseSignatureFile").val();
        if (/^\s*$/.test(filename)) {
            $("#signature").removeClass('active');
            $("#btnSubmitSignature").removeClass('active');
            $("#btnSubmitSignature").removeClass('warning');
            $("#noSignatureFileFile").text("No file chosen...");

        }
        else {
            $("#signature").addClass('active');
            $("#noSignatureFile").text(filename.replace("C:\\fakepath\\", ""));
        }
    });


});

function fire_ajax_compare(){
    var form = $("#compareFileUploadForm")[0];
    var formData = new FormData(form);

    var name1 = $("#chooseSignatureFile").val();
    var name2 = $("#chooseSecondaryFile").val();

    if(name1 != "" && name2 != ""){
        $.ajax({
            type:"POST",
            enctype:"multipart/form-data",
            url:"/api/check-signature",
            data:formData,
            processData: false,
            contentType: false,
            cache:false,
            timeout: 60000,
            success:function(data){
                if(data == true){
                    $("#error").hide();
                    $("#success").show();
                }
                else{
                    $("#success").hide();
                    $("#error").show();
                }
            },
            error:function (e) {
                console.log("ERROR : ",e);
                $("#btnSubmitDigitalSignature").prop("disabled", false);
            }
        });
    }
}



function fire_ajax_submit(){
    //Get Form
    var form = $("#fileUploadForm")[0];
    var data = new FormData(form);

    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type:"POST",
        enctype : 'multipart/form-data',
        url : "/api/upload/multi",
        data: data,
        processData: false, //Prevent jQuery from automatically transforming the data into a to a query string
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
            $("#fileTextAsymmetric").val(data);
            $("#fileTextSymmetric").val(data);

        },
        error: function(e){
            console.log("ERROR : ",e);
            $("#btnSubmit").prop("disabled", false);
        }
    }).done(function () {
        fire_ajax_hash();
    });

}

function fire_ajax_hash() {
    $.ajax({
        type: "POST",
        url: "/api/hash",
        success: function (data) {
            $("#fileHash").val(data);
        },
        error: function (e) {
            console.log("Error: ", e);
        }
    }).done(function () {
        fire_ajax_digital_signature();
    });
}


function fire_ajax_generate_keys(){
    $.ajax({
       type:"POST",
       url:"/api/keys",
       success:function (data) {
           $("#secretKey").val(data.SecretKey);
           $("#publicKey").val(data.PublicKey);
           $("#privateKey").val(data.PrivateKey);
       },
        error:function(e){
           console.log("Error: ",e);
        }
    });
}

function fire_ajax_symmetric(operation){
    $.ajax({
       type:"POST",
       url:"/api/symmetric",
       data:{
           operation:operation
       },
        success:function (data) {
            $("#fileTextSymmetric").val(data)
        },
        error:function(e){
           console.log("Error: ",e)
        }
    })
}

function  fire_ajax_asymmetric(operation){
    $.ajax({
       type:"POST",
        url:"/api/asymmetric",
        data:{
           operation: operation
        },
        success:function(data){
           $("#fileTextAsymmetric").val(data)
        },
        error:function (e) {
            console.log("ERROR : ",e);
        }
    })
}

function fire_ajax_digital_signature(){
    $.ajax({
        type:"POST",
        url:"/api/digital-signature",
        success:function (data) {
            $("#digitalSignature").val(data)
        },
        error:function(e){
            console.log("Error: ",e)
        }
    });
}

