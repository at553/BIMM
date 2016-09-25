$(document).ready(function() {
  $.ajax({
	  data:{aimInputUID :getUrlParam("annotationID")},
	  type: 'GET',
	  url: '/BIMM/bimmservlet',
	  success: function(response) {
		  populateImageFromInputParam();
          populateThumbnailImages(response);
          toggleRegisterButton(false);
	  },
	  error: function (xhr, ajaxOptions, thrownError) {
  		alert("EPad Processing error. Please contact administrator");
  	}
  })

 /* $('#bimmform').submit(function(event) {
    event.preventDefault();
    clearContent();
    $.ajax({ // create an AJAX call...
        data: $(this).serialize(), // get the form data
        type: $(this).attr('method'), // GET or POST
        url: $(this).attr('action'), // the file to call
        success: function(response) { // on success..
           populateImage();
           populateThumbnailImages(response);
           toggleRegisterButton(false);
        },
    	error: function (xhr, ajaxOptions, thrownError) {
    		alert("EPad Processing error. Please contact administrator");
    	}
    });
  });
  */
  $("#register").click (function(){
	  var feedbackResults = getRadioResponses();
	  
	  $.post("/BIMM/indexingservlet", JSON.stringify(feedbackResults), function(response){
		  $("#thumbnail").empty();
		  populateThumbnailImages(response);
	  }, 'json');
	  console.log(feedbackResults);
	  
  });
  
  $(document).ajaxStart(function(){
	  $(document.body).css({'cursor' : 'wait'});
	});

  $(document).ajaxComplete(function(){
	  $(document.body).css({'cursor' : 'default'});
	});
  function toggleRegisterButton(value)
  {
	  $("#register").prop('disabled', value);
  }
  function populateImage()
  {  
      var uid = $("#usr").val();
      console.log(uid)
      var path = 'img/imageStore/' + uid + '.jpg';
      console.log(path);
      $("#selecteduid").append("<img height='200' width='200' src=" + "'"+ path +"'" + "/>");
      $("#selecteduid").append("<p>"+uid +"</p>");
  }
  
  function populateImageFromInputParam()
  {
	  var uid = getUrlParam("annotationID");
	  if (uid )
	  {
		  var path = 'img/imageStore/' + uid + '.jpg';
	      console.log(path);
	      $("#selecteduid").append("<img height='200' width='200' src=" + "'"+ path +"'" + "/>");
	      $("#selecteduid").append("<p>"+uid +"</p>");
	  }
  }
    
//  function populateThumbnailImages(response)
//  {
//	  console.log(response);
//      var numRes = $($(response).find("QueryResults")[0]).attr("numberOfResults");
//      if (numRes > 0){
//    	  var currentImage = 0;
//    	  var currentRowId;
//    	  
//    	  $(response).find("result").each(function(){
//    		  var aimUID = $(this).attr("aimUID");
//    		  
//    		  if (currentImage % 3 == 0) {
//    			  createRow(aimUID);
//    			  currentRowId = aimUID;
//    		  }
//    		  createColumn(currentRowId, currentImage, aimUID);
//    		  currentImage += 1;
//    	  })
//      }
//      
//  }
//  
  
function populateThumbnailImages(response)
{
   console.log(response.length);
   var numRes = response.length;
   if (numRes > 0){
  	  var currentImage = 0;
  	  var currentRowId;
  	  $(response).each(function(index, value){
  		  var aimUID = value;  		  
  		  if (currentImage % 6 == 0) {
  			  createRow(aimUID);
 			  currentRowId = aimUID;
 		  }
 		  createColumn(currentRowId, currentImage, aimUID);
  		  currentImage += 1;
  	  })
   }
    
  }

  function createRow(rowId)
  {
	  $("#thumbnail").append("<div class=\"row\" id=\"" + rowId.replace('.','') + "\"></div>");
  }
  
  function getRadioResponses(rowID)
  {
	  var positives = '';
	  var negatives = '';
	  var feedback = {positives: [], negatives:[]};
	  //alert($('input[name=relevance_' + rowID + ']:checked', '#form_' + rowID).val());
	  $('input:radio').each(function(){
		  if ($(this).is(":checked"))
			 // console.log ($(this).attr('name') + ':' + $(this).val());
		      if ($(this).val() === 'irrelevant')
		      {
		    	  var uid = $(this).attr('name').split("_")[1];
		    	  negatives = negatives + uid + ',';
		      }
		      else if ($(this).val() === 'relevant')
		      {
		    	  var uid = $(this).attr('name').split("_")[1];
		    	  positives = positives + uid + ',';
		      }
	  });
	  //var selectedUID = $("#usr").val();
	  var selectedUID = getUrlParam("annotationID");
	  return { positives: positives, negatives: negatives, selectedUID: selectedUID};
  }
  
  function createColumn(currentRowId, currentImage, aimUID)
  {
	  var rowId = currentRowId.replace('.','');
	  var colId = currentRowId.replace('.', '') + "" + currentImage;
	  $("#" + rowId).append("<div class=\"col-md-2\" id =\"" + colId + "\"></div>");
	  
	  $("#" + colId).append("<a class=\"thumbnail\" id=\"" +colId + "_thumbnail\" title = \""+ aimUID +"\"></a>");
	  $("#" + colId + "_thumbnail").append("<img src=\"img/imageStore/" + aimUID + ".jpg\" height alt=\"125x125\"></img>");
	  $("#" + colId + "_thumbnail").append("<form id=\"form_" + colId + "\"><input type=\"radio\" name=\"relevance_" + aimUID + "\" value=\"relevant\" > Relevant<br><form><input type=\"radio\" name=\"relevance_" + aimUID + "\" value=\"neither\" checked> No Opinion<br><form><input type=\"radio\" name=\"relevance_" + aimUID + "\" value=\"irrelevant\"> Irrelevant<br>");
	  
  }
  
  function clearContent()
  {
	  $("#selecteduid").empty();
	  $("#thumbnail").empty();
	  toggleRegisterButton(true);
  }
  
  function getUrlParam(name){
		var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
		return results[1] || 0;
  }

});