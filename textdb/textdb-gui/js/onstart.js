/*
	Gui <---> Flowchart.js Communication
	GUIJSON <---> TEXTDBJSON Conversion
	
	@Author: Jimmy Wang
*/

$(document).ready(function() {
    var data = {};

    // Apply the plugin on a standard, empty div...
    $('#the-flowchart').flowchart({
      data: data
    });
	
	var operatorI = 0;
	
	var regexInput = "zika\s*(virus|fever)";
	var keywordInput = "Zika";
	var defaultDict = "file://SampleDict1";
	var fuzzyInput = "FuzzyWuzzy";
	var thresholdRatio = 0.8;
	var nlpArray = ["noun", "verb", "adjective", "adverb", "ne_all", "number", "location", "person", "organization", "money", "percent", "date", "time"];
	var nlpInput = "ne_all";
	var defaultAttributes = "first name, last name";
	var defaultLimit = 10;
	var defaultOffset = 5;


/*
	Process Operators to Server (GUIJSON --> TEXTDBJSON --> Server)
*/


	$('.process-query').on('click', function() {
		
		var GUIJSON = $('#the-flowchart').flowchart('getData');
		
		var TEXTDBJSON = {};
		var operators = [];
		var links = [];
		
		for(var operatorIndex in GUIJSON.operators){
			if (GUIJSON.operators.hasOwnProperty(operatorIndex)){
				var attributes = {};
				
				for(var attribute in GUIJSON['operators'][operatorIndex]['properties']['attributes']){
					if (GUIJSON['operators'][operatorIndex]['properties']['attributes'].hasOwnProperty(attribute)){
						attributes[attribute] = GUIJSON['operators'][operatorIndex]['properties']['attributes'][attribute];
					}
				}
				operators.push(attributes);
			}
		}	
		
		for(var link in GUIJSON.links){
			var destination = {};
			if (GUIJSON['links'][link].hasOwnProperty("fromOperator")){
				destination["from"] = GUIJSON['links'][link]['fromOperator'];
				destination["to"] = GUIJSON['links'][link]['toOperator'];
				links.push(destination);
			}
		}
		TEXTDBJSON.operators = operators;
		TEXTDBJSON.links = links;
		
		// console.log(operators);
		// console.log(links)
		// console.log(data);
		// console.log(JSON.stringify(data));
		// console.log(JSON.stringify(data2));
		
		$.ajax({
			url: "http://localhost:8080/queryplan/execute", 
			type: "POST",
			dataType: "json",
			//contentType: "application/json; charset=utf-8",
			//headers: { 'Access-Control-Allow-Origin': '*' },
			//crossDomain: true,
			//data: JSON.stringify(TEXTDBJSON),
			success: function(returnedData){
				console.log("SUCCESS\n");
				console.log(JSON.stringify(returnedData));
			},
			error: function(xhr, status, err){
				console.log("ERROR");
				console.log(err);
			}
		});
	});


/*
	Attribute Pop-Up Box
*/

	
	$('#the-flowchart').on('click', '.flowchart-operators-layer.unselectable div .flowchart-operator-title', function() {
		var selectedOperator = $('#the-flowchart').flowchart('getSelectedOperatorId');
		var output = data['operators'][selectedOperator]['properties']['attributes'];
		var title = data['operators'][selectedOperator]['properties']['title'];
		// console.log(data['operators'][selectedOperator]['attributes']);
		// console.log(JSON.stringify(data['operators'][selectedOperator]['attributes']));
		// console.log(output);
		// console.log(JSON.stringify(output));
		
		$('.popup').animate({
            'bottom': '0'
        }, 200);
		
		$('#attributes').css({
			'visibility': 'visible'
		});
		
		$('#attributes').text(function(){
			var result = "";
			for(var attr in output){
				if(attr == 'operator_id'){
					continue;
				}
				if(output.hasOwnProperty(attr)){
					var splitString = attr.split("_");
					for(var splitPart in splitString){
						result += ' ' + splitString[splitPart].charAt(0).toUpperCase() + splitString[splitPart].slice(1);
					}
					result += ": ";
					result += output[attr] + "\n";
				}
			}
			result += "\n";
			result.replace(/\n/g, '<br />');
			return result;
		});
		
		var editButton = $('<button class="edit-operator">Edit</button>');
        $('#attributes').append(editButton);
		
		var deleteButton = $('<button class="delete-operator">Delete</button>');
        $('#attributes').append(deleteButton);
		
		$('#attributes button').css({
			'margin': '5px',
			'border': '1px solid black'
		});
		
		$('.band').html('Attributes for <em>' + title + '</em>');
	});

	
/*
	Create Operator Helper Functions
*/


	function getExtraOperators(userInput, panel){
	  var extraOperators = {};
	  
	  if (panel == 'regex-panel'){
		if (userInput == null || userInput == ''){
			userInput = regexInput;
		}
	    extraOperators['regex'] = userInput;
	  }
	  else if (panel == 'keyword-panel'){
		if (userInput == null || userInput == ''){
			userInput = keywordInput;
		}
		extraOperators['keyword'] = userInput;
		extraOperators['matching_type'] = $('#keywordMatchingType').val();
	  }
	  else if (panel == 'dictionary-panel'){
		if (userInput == null || userInput == ''){
			userInput = defaultDict;
		}
		extraOperators['dictionary'] = userInput;
		extraOperators['matching_type'] = $('#dictionaryMatchingType').val();
	  }
	  else if (panel == 'fuzzy-panel'){
		if (userInput == null || userInput == ''){
			userInput = fuzzyInput;
		}
		extraOperators['query'] = userInput;
		extraOperators['threshold_ratio'] = thresholdRatio;
	  }
	  else if (panel == 'nlp-panel'){
		if (userInput == null || userInput == ''){
			userInput = nlpInput;
		}
		else if(nlpArray.indexOf(userInput.toLowerCase()) == -1){
			alert('Please choose an NLP from the following: ["noun", "verb", "adjective", "adverb", "ne_all", "number", "location", "person", "organization", "money", "percent", "date", "time"]');
			return;
		}
		extraOperators['nlp-type'] = userInput;
	  }
	  return extraOperators;
	}
	
	function getAttr(panel, keyword){
		var result = $('#' + panel + keyword).val();
		if(keyword == ' .limit'){
			if (result == null || result == ''){
				result = defaultLimit;
			}
		}
		else if(keyword == ' .offset'){
			if (result == null || result == ''){
				result = defaultOffset;
			}
		}
		else if(keyword == ' .attributes'){
			if (result == null || result == ''){
				result = defaultAttributes;
			}
		}
		return result;
	}

	
/*
	Create Operator Button
*/


    $('.create-operator').click(function() {
	  var panel = $(this).attr('rel');

	  var userInput = $('#' + panel + ' .value').val();	  
	  var extraOperators = getExtraOperators(userInput,panel);
	  
	  var userLimit = getAttr(panel, ' .limit');
	  var userOffset = getAttr(panel, ' .offset');
	  var userAttributes = getAttr(panel, ' .attributes');
	  var operatorName = $('#' + panel + ' button').attr('id');
      var operatorId = operatorName + '_' + operatorI;
      var operatorData = {
        top: 60,
        left: 500,
        properties: {
          title: 'Temp',
          inputs: {
            input_1: {
              label: 'Input 1',
            }
          },
          outputs: {
            output_1: {
              label: 'Output 1',
            }
          },
		  attributes: {
			operator_id: (operatorId),
			operator_type: (operatorName),
		  }
        }
      };
      
	  var count = 0;
	  for(var extraOperator in extraOperators){
		  if(count == 0){
			operatorData.properties.title = extraOperators[extraOperator];
			count++;
		  }
		  operatorData.properties.attributes[extraOperator] = extraOperators[extraOperator];
	  }
	  operatorData.properties.attributes['attributes'] = userAttributes;
	  operatorData.properties.attributes['limit'] = userLimit;
	  operatorData.properties.attributes['offset'] = userOffset;
	  
      operatorI++;
      
      $('#the-flowchart').flowchart('createOperator', operatorId, operatorData);
	  
	  data = $('#the-flowchart').flowchart('getData');
    });


/*
	Edit Operator Button
*/


	
	$('#attributes').on('click', '.edit-operator', function() {
		
	});
	
	
/*
	Delete Operator Button
*/



	$('.delete-operator').on('click', function() {
		$('#attributes').css({
			'visibility': 'hidden'
		});
		
		$('.band').text('Attributes');
		
		$('#the-flowchart').flowchart('deleteSelected');
		data = $('#the-flowchart').flowchart('getData');
    });
	
	$('#attributes').on('click', '.delete-operator', function() {
		$('#attributes').css({
			'visibility': 'hidden'
		});
		
		$('.band').text('Attributes');
		
		$('#the-flowchart').flowchart('deleteSelected');
		data = $('#the-flowchart').flowchart('getData');
    });
});