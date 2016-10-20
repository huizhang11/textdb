$(document).ready(function() {
    var data = {};

    // Apply the plugin on a standard, empty div...
    $('#the-flowchart').flowchart({
      data: data
    });
	
	var operatorI = 0;
	var operatorName = 'Default';
	
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
	
	$('.process_operators').click(function() {
		
		var GUIJSON = $('#the-flowchart').flowchart('getData');
		
		var data = {};
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
		data.operators = operators;
		data.links = links;
		
		// console.log(operators);
		// console.log(links)
		// console.log(data);
		console.log(JSON.stringify(data, null, 4));
		
		// makeTextFile = function (text) {
			// var data = new Blob([text], {type: 'text/plain'});

		// // If we are replacing a previously generated file we need to
		// // manually revoke the object URL to avoid memory leaks.
			// if (textFile !== null) {
				// window.URL.revokeObjectURL(textFile);
			// }

			// textFile = window.URL.createObjectURL(data);

		// // returns a URL you can use as a href
			// return textFile;
		// };
		
		// window.location.assign(makeTextFile(str));
	});
	
    $('.create_operator').click(function() {
	  var panel = $(this).attr('rel');
	  var userInput = 'Default Input';
	  var userLimit = defaultLimit;
	  var userOffset = defaultOffset;
	  var userAttributes = defaultAttributes;
	  
	  var extraOperators = {};
	  
	  if (panel == 'regex-panel'){
		userInput = $('#regexText').val();
		if (userInput == null || userInput == ''){
			userInput = regexInput;
		}
		userAttributes = $('#regexAttributes').val();
		if (userAttributes == null || userAttributes == ''){
			userAttributes = defaultAttributes;
		}
		operatorName = 'RegexMatcher';
		userLimit = $('#regexLimit').val();
		userOffset = $('#regexOffset').val();
		extraOperators['regex'] = userInput;
	  }
	  else if (panel == 'keyword-panel'){
		userInput = $('#keywordText').val();
		if (userInput == null || userInput == ''){
			userInput = keywordInput;
		}
		userAttributes = $('#keywordAttributes').val();
		if (userAttributes == null || userAttributes == ''){
			userAttributes = defaultAttributes;
		}
		operatorName = 'KeywordMatcher';
		userLimit = $('#keywordLimit').val();
		userOffset = $('#keywordOffset').val();
		extraOperators['keyword'] = userInput;
		extraOperators['matching_type'] = $('#keywordMatchingType').val();
	  }
	  else if (panel == 'dictionary-panel'){
		userInput = $('#dictionaryFile').val();
		if (userInput == null || userInput == ''){
			userInput = defaultDict;
		}
		userAttributes = $('#dictionaryAttributes').val();
		if (userAttributes == null || userAttributes == ''){
			userAttributes = defaultAttributes;
		}
		operatorName = 'DictionaryMatcher';
		userLimit = $('#dictionaryLimit').val();
		userOffset = $('#dictionaryOffset').val();
		extraOperators['dictionary'] = userInput;
		extraOperators['matching_type'] = $('#dictionaryMatchingType').val();
	  }
	  else if (panel == 'fuzzy-panel'){
		userInput = $('#fuzzyText').val();
		if (userInput == null || userInput == ''){
			userInput = fuzzyInput;
		}
		userAttributes = $('#fuzzyAttributes').val();
		if (userAttributes == null || userAttributes == ''){
			userAttributes = defaultAttributes;
		}
		operatorName = 'FuzzyTokenMatcher';
		userLimit = $('#fuzzyLimit').val();
		userOffset = $('#fuzzyOffset').val();
		extraOperators['query'] = userInput;
		extraOperators['threshold_ratio'] = thresholdRatio;
	  }
	  else if (panel == 'nlp-panel'){
		userInput = $('#nlpText').val();
		if (userInput == null || userInput == ''){
			userInput = nlpInput;
		}
		else if(nlpArray.indexOf(userInput.toLowerCase()) == -1){
			alert('Please choose an NLP from the following: ["noun", "verb", "adjective", "adverb", "ne_all", "number", "location", "person", "organization", "money", "percent", "date", "time"]');
			return;
		}
		userAttributes = $('#nlpAttributes').val();
		if (userAttributes == null || userAttributes == ''){
			userAttributes = defaultAttributes;
		}
		operatorName = 'NlpExtractor';
		userLimit = $('#nlpLimit').val();
		userOffset = $('#nlpOffset').val();
		extraOperators['nlp-type'] = userInput;
	  }
      var operatorId = operatorName + '_' + operatorI;
      var operatorData = {
        top: 60,
        left: 500,
        properties: {
          title: (userInput),
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
    });
	
	$('#delete-panel').click(function() {
      $('#the-flowchart').flowchart('deleteSelected');
    });
	
});