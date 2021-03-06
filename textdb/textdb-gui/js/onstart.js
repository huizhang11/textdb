/*
	Gui <---> Flowchart.js Communication
	GUIJSON <---> TEXTDBJSON Conversion
	
	@Author: Jimmy Wang
*/

// main operation (holds all the methods for the buttons and holds the buttons itself)
var setup = function(){
	var data = {};

	// Apply the plugin on a standard, empty div...
	$('#the-flowchart').flowchart({
		data: data
	});
	
	var operatorI = 0;
	var selectedOperator = '';
	var editOperators = [];
	
	var DEFAULT_KEYWORD = "Zika";
	var DEFAULT_REGEX = "zika\s*(virus|fever)";
	var DEFAULT_ATTRIBUTES = "first name, last name";
	var DEFAULT_LIMIT = 10;
	var DEFAULT_OFFSET = 5;
	
	/*
		Helper Functions
	*/
	
	//Create Operator Helper Function 
	function getExtraOperators(userInput, panel){
	  var extraOperators = {};
	  
	  if (panel == 'regex-panel'){
		if (userInput == null || userInput == ''){
			userInput = DEFAULT_REGEX;
		}
	    extraOperators['regex'] = userInput;
	  }
	  else if (panel == 'keyword-panel'){
		if (userInput == null || userInput == ''){
			userInput = DEFAULT_KEYWORD;
		}
		extraOperators['keyword'] = userInput;
		extraOperators['matching_type'] = $('#' + panel + ' .matching-type').val();
	  }
	  return extraOperators;
	};
	
	//Create Operator Helper Function 
	function getAttr(panel, keyword){
		var result = $('#' + panel + keyword).val();
		if(keyword == ' .limit'){
			if (result == null || result == ''){
				result = DEFAULT_LIMIT;
			}
		}
		else if(keyword == ' .offset'){
			if (result == null || result == ''){
				result = DEFAULT_OFFSET;
			}
		}
		else if(keyword == ' .attributes'){
			if (result == null || result == ''){
				result = DEFAULT_ATTRIBUTES;
			}
		}
		return result;
	};
	
	//Edit Operator Button Helper Function
		//getEditInputHtml Helper Function (get Html for a specific operator)
	function getHtml(attr, attrValue){
		var resultString = '';
		var classString = attr.replace(/_/g, '-');
		var resultString = '';
		var classString = attr.replace(/_/g, '-');
		if(attr == 'matching_type'){
			resultString += '<select class="matching-type"><option value="conjunction"';
			if(attrValue == 'conjunction'){
				resultString += ' selected';
			}
			resultString += '>Conjunction</option><option value="phrase"';
			if(attrValue == 'phrase'){
				resultString += ' selected';
			}
			resultString += '>Phrase</option><option value="substring"';
			if(attrValue == 'substring'){
				resultString += ' selected';
			}
			resultString += '>Substring</option></select>';
		}
		else{
			resultString += '<input type="text" class="' + classString + '" value="' + attrValue + '">';
		}
		editOperators.push(classString);
		return resultString;		
	}
	
	//Edit Operator Help Function (creates the html code for the input boxes to edit an operator)
	var getEditInputHtml = function(output){
		var result = "";
		for(var attr in output){
			if(attr == 'operator_id'){
				continue;
			}
			else if(output.hasOwnProperty(attr)){
				var splitString = attr.split("_");
				for(var splitPart in splitString){
					result += ' ' + splitString[splitPart].charAt(0).toUpperCase() + splitString[splitPart].slice(1);
				}
				result += ": ";
				if(attr == 'operator_type'){
					result += output[attr] + "\n";					
				}
				else{
					var inputHtml = getHtml(attr,output[attr]);
					result += "\t" + "\n";
					result = result.replace(/\t/g, inputHtml);
				}
			}
		}
		result += "\n";
		result.replace(/\n/g, '<br />');
		return result;
	};
	
	//Attribute Pop-Up Box Helper Function
	function getPopupText(output){
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
				result += '<b>' + output[attr] + '</b>' + '<br />';
			}
		}
		result += '<br />';
		return result;
	};

	/*
		Button functions
	*/
	
	//Process Operators to Server (GUIJSON --> TEXTDBJSON --> Server)
	var processQuery = function(){
		var GUIJSON = $('#the-flowchart').flowchart('getData');
			
		var TEXTDBJSON = {};
		var operators = [];
		var links = [];
		
		for(var operatorIndex in GUIJSON.operators){
			if(GUIJSON.operators.hasOwnProperty(operatorIndex) {
				var attributes = {};
				var currentOperator = GUIJSON['operators'][operatorIndex];
				for(var attribute in currentOperator['properties']['attributes']){
					if (currentOperator['properties']['attributes'].hasOwnProperty(attribute)){
						attributes[attribute] = currentOperator['properties']['attributes'][attribute];
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
	
		// console.log(JSON.stringify(TEXTDBJSON));
		// console.log(JSON.stringify(GUIJSON));
		
		$.ajax({
			url: "http://localhost:8080/queryplan/execute",
			type: "POST",
			data: JSON.stringify(TEXTDBJSON),
			dataType: "text",
			contentType: "application/json",
			success: function(returnedData){
				console.log("SUCCESS\n");
				console.log(JSON.stringify(returnedData));
			},
			error: function(xhr, status, err){
				console.log("ERROR");
			}
		});
	};
	
	//Attribute Pop-Up Box displays attributes in the popup box for selected operator
	var displayPopupBox = function(){
		selectedOperator = $('#the-flowchart').flowchart('getSelectedOperatorId');
		var output = data['operators'][selectedOperator]['properties']['attributes'];
		var title = data['operators'][selectedOperator]['properties']['title'];
		
		$('.popup').animate({
            'bottom': '0'
        }, 200);
		
		$('#attributes').css({
			'visibility': 'visible'
		});
		
		$('#attributes').text(getPopupText(output));
		$('#attributes').html($('#attributes').text());
		
		var editButton = $('<button class="edit-operator">Edit</button>');
        $('#attributes').append(editButton);
		
		var deleteButton = $('<button class="delete-operator">Delete</button>');
        $('#attributes').append(deleteButton);
		
		$('.band').html('Attributes for <em>' + title + '</em>');
	};	
	
	//Create Operator to send to flowchart.js and display on flowchart
	var createOperator = function(buttonPanel){
		var panel = $(buttonPanel).attr('rel');

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
				title: (operatorName),
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

		for(var extraOperator in extraOperators){
		  operatorData.properties.attributes[extraOperator] = extraOperators[extraOperator];
		}
		operatorData.properties.attributes['attributes'] = userAttributes;
		operatorData.properties.attributes['limit'] = userLimit;
		operatorData.properties.attributes['offset'] = userOffset;

		operatorI++;

		$('#the-flowchart').flowchart('createOperator', operatorId, operatorData);

		data = $('#the-flowchart').flowchart('getData'); 
	};
	
	//Edit Operator to send to flowchart.js and display on flowchart
	var editOperator = function(){
		editOperators = [];
		var output = data['operators'][selectedOperator]['properties']['attributes'];
		
		$('#attributes').text(getEditInputHtml(output));
		
		$('#attributes').html($('#attributes').text());
		
		var confirmChangesButton = $('<button class="confirm-button">Confirm Changes</button>');
        $('#attributes').append(confirmChangesButton);
		
		var cancelButton = $('<button class="cancel-button">Cancel</button>');
        $('#attributes').append(cancelButton);
	};
	
	//Confirms changes made to selected operator which are passed to flowchart.js and recreated.
	var confirmChanges = function(){
		data = $('#the-flowchart').flowchart('getData');
		var output = data['operators'][selectedOperator]['properties']['attributes'];
		var panel = $(this).parent().attr('class');

		var operatorTop = data['operators'][selectedOperator]['top'];
		var operatorLeft = data['operators'][selectedOperator]['left'];
		var operatorId = output['operator_id'];
		var operatorName = output['operator_type'];

		var operatorData = {
			top: (operatorTop),
			left: (operatorLeft),
			properties: {
				title: (operatorName),
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
	  
		for(var otherOperator in editOperators){
			var attr = editOperators[otherOperator].replace(/-/, '_');
			var result = $('.' + panel + ' .' + editOperators[otherOperator]).val();
			if(((result == '') || (result == null)) && (attr == 'dictionary')){
				result = defaultDict;
			}
			operatorData.properties.attributes[attr] = result;
		}
      
		$('#the-flowchart').flowchart('deleteSelected');
		$('#the-flowchart').flowchart('createOperator', operatorId, operatorData);
		$('#the-flowchart').flowchart('selectOperator', operatorId);
		selectedOperator = $('#the-flowchart').flowchart('getSelectedOperatorId');

		data = $('#the-flowchart').flowchart('getData');
		output = data['operators'][selectedOperator]['properties']['attributes'];

		var title = data['operators'][selectedOperator]['properties']['title'];
		$('.band').html('Attributes for <em>' + title + '</em>');

		$('#attributes').text(getPopupText(output));
		$('#attributes').html($('#attributes').text());

		var editButton = $('<button class="edit-operator">Edit</button>');
		$('#attributes').append(editButton);

		var deleteButton = $('<button class="delete-operator">Delete</button>');
		$('#attributes').append(deleteButton);
	};
	
	//Deletes the selected operator from flowchart.js and GUI
	var deleteOperator = function(){
		data = $('#the-flowchart').flowchart('getData');
		$('#attributes').css({
			'visibility': 'hidden'
		});
		
		$('.band').text('Attributes');
		
		$('#the-flowchart').flowchart('deleteSelected');
		data = $('#the-flowchart').flowchart('getData');
	};
	
	/*
		Buttons
	*/
	
	//Process Operator Button. Calls processQuery function
	$('.process-query').on('click', processQuery);

	//Upon Operator Selection (when operator is clicked/selected) Calls displayPopupBox function
	$('#the-flowchart').on('click', '.flowchart-operators-layer.unselectable div .flowchart-operator-title', displayPopupBox);

	//Create Operator button. Calls createOperator function
	$('.create-operator').click(function() {
		createOperator(this);
	});

	//Edit Operator Button calls editOperator function
	$('#attributes').on('click', '.edit-operator', editOperator);	

	//Confirm Changes Button. Calls confirmChanges function
	$('#attributes').on('click', '.confirm-button', confirmChanges);

	//Cancel Edit Button (as if selecting the operator again) calls displayPopupBox function
	$('#attributes').on('click', '.cancel-button', displayPopupBox);
		
	//Delete Operator Button. Calls deleteOperator function
	$('#attributes, .nav').on('click', '.delete-operator', deleteOperator);
};

$(document).ready(setup);
