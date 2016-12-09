package edu.uci.ics.textdb.sandbox.terminalapp;

import junit.framework.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.textdb.textql.languageparser.ParseException;
import edu.uci.ics.textdb.textql.languageparser.TextQLParser;
import edu.uci.ics.textdb.textql.languageparser.TokenMgrError;

public class TerminalAppTest {


    @Before
    public void setUp() {
        //TokenMgrError: did not match any token;
        //ParseException: got the wrong kind of token;
    }

	
	/*
	return new HashMap<String, Object>() {{
        put("statementType", "select");
        put("statementName", name);
        put("projectAll", projectFields!=null&&projectFields.size()==0?true:null);
        put("projectFields", projectFields!=null&&projectFields.size()>0?projectFields:null);
        put("extractCommand", extract);
        put("from", from);
        put("limit", limit);
        put("offset", offset);
    }};
    return new HashMap<String, Object>() {{
        put("extractType", "keyword");
        put("matchFields", matchFields);
        put("keyword", keyword);
        put("matchType", matchType);
    }};
    return new HashMap<String, Object>() {{
        put("statementType", "view");
        put("statementName", viewName);
        put("substatement", substatement);
    }};
    {
	    "operator_type" : "KeywordSource",
	    "data_source" : "data_source_name",
	    "keyword" : "a_keyword",
	    "matching_type" : "one of: [conjunction, phrase, substring]"
	}
	{
	    "operator_type" : "Projection",
	}
	{
	    "operator_type" : "KeywordMatcher",
	    "keyword" : "a_keyword",
	    "matching_type" : "one of: [conjunction, phrase, substring]"
	}
	{
	    "attributes" : "attr1_name, attr2_name, attr3_name",
	    "limit" : "10 (this property is optional)",
	    "offset" : "5 (this property is optional)"
	}
	{
	    "operator_type" : "FileSink",
	    "file_path" : "file_path"
	}
	*/



/*
 * Steps:
 * Run a BFS from output view node
 * 	- For View: create a pass-through operator with the name of the view
 * 	- For Select: create a source->extract->projection and check if it can be simplified (no extraction, SELECT *=>no projection, merge source and extract into source)
 *  - When a select uses a view as source, try to do the same simplification and try create a pass through (remove pass-through, merge source and extract into source)

	Test Cases:
{
	0. CREATE VIEW out AS SELECT * FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0s",
						"operator_type" : "ScanSource",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0s",
		                "to" : "out"
		        }
	        ]
	}
	5. CREATE VIEW out AS SELECT a,b FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0s",
						"operator_type" : "ScanSource",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "__lid0p",
						"operator_type" : "Projection",
						"attributes" : "a,b"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
	        {
	                "from" : "__lid0s",
	                "to" : "__lid0p"
	        },
		        {
		                "from" : "__lid0p",
		                "to" : "out"
		        }
	        ]
	}
	8. CREATE VIEW out AS EXTRACT KEYWORDMATCH([a,b],"key") FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordSource",
						"attributes" : "a,b"
					    "keyword" : "key",
					    "matching_type" : "conjunction",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0e",
		                "to" : "out"
		        }
	        ]
	}
	0. CREATE VIEW out AS EXTRACT KEYWORDMATCH(a,"key",substring) FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordSource",
						"attributes" : "a",
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0e",
		                "to" : "out"
		        }
	        ]
	}
	1. CREATE VIEW out AS SELECT * FROM t LIMIT 5 OFFSET 1;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0s",
						"operator_type" : "ScanSource",
						"data_source" : "t",
					    "limit" : "5",
					    "offset" : "1"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0s",
		                "to" : "out"
		        }
	        ]
	}
	1. CREATE VIEW out AS EXTRACT KEYWORDMATCH(a,"key",substring) FROM t LIMIT 5 OFFSET 1;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordSource",
						"attributes" : "a",
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t",
					    "limit" : "5",
					    "offset" : "1"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0e",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS SELECT * FROM b; CREATE VIEW b AS SELECT * FROM a;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid1s",
						"operator_type" : "ScanSource",
						"data_source" : "a",
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1s",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS SELECT * FROM b; CREATE VIEW b AS EXTRACT KEYWORDMATCH(a,"key", substring) FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid1e",
						"operator_type" : "KeywordSource",
						"attributes" : "a"
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t",
					    "limit" : "5",
					    "offset" : "1"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1e",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS EXTRACT KEYWORDMATCH(a,"key", substring) FROM b; CREATE VIEW b AS SELECT * FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordSource",
						"attributes" : "a"
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t",
					    "limit" : "5",
					    "offset" : "1"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
	        {
	                "from" : "__lid0e",
	                "to" : "out"
	        }
	        ]
	}
	2. CREATE VIEW out AS SELECT a,b FROM t; CREATE VIEW t AS EXTRACT KEYWORDMATCH(a,"key", substring) FROM t2;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0p",
						"operator_type" : "Projection",
						"attributes" : "a, b"
		        },
		        {
		                "operator_id" : "__lid1e",
						"operator_type" : "KeywordSource",
						"attributes" : "a"
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "b"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1e",
		                "to" : "__lid0p"
		        },
		        {
		                "from" : "__lid0p",
		                "to" : "out"
		        }
	        ]
	}
	//3. CREATE VIEW out AS CREATE VIEW b AS SELECT * FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0s",
						"operator_type" : "ScanSource",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0s",
		                "to" : "out"
		        }
	        ]
	}
	6. CREATE VIEW out AS SELECT * EXTRACT KEYWORDMATCH([a,b],"key") FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordSource",
						"attributes" : "a,b"
					    "keyword" : "key",
					    "matching_type" : "conjunction",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0e",
		                "to" : "out"
		        }
	        ]
	}
	7. CREATE VIEW out AS SELECT a,b EXTRACT KEYWORDMATCH([a,b],"key") FROM t;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0p",
						"operator_type" : "Projection",
						"attributes" : "a,b"
		        },
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordSource",
						"attributes" : "a,b"
					    "keyword" : "key",
					    "matching_type" : "conjunction",
						"data_source" : "t"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid0e",
		                "to" : "__lid0p"
		        },
		        {
		                "from" : "__lid0p",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS EXTRACT KEYWORDMATCH(b,"keywo") FROM t; CREATE VIEW t AS SELECT a,b EXTRACT KEYWORDMATCH(a,"key", substring) FROM t2;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordMatch",
						"attributes" : "b"
					    "keyword" : "keywo",
					    "matching_type" : "conjunction"
		        },
		        {
		                "operator_id" : "__lid1p",
						"operator_type" : "Projection",
						"attributes" : "a,b"
		        },
		        {
		                "operator_id" : "__lid1e",
						"operator_type" : "KeywordSource",
						"attributes" : "a,b"
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t2"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1e",
		                "to" : "__lid1p"
		        },
		        {
		                "from" : "__lid1p",
		                "to" : "__lid0e"
		        },
		        {
		                "from" : "__lid0e",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS EXTRACT KEYWORDMATCH(b,"keywo") FROM t; CREATE VIEW t AS SELECT a,b FROM t2;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordMatch",
						"attributes" : "b"
					    "keyword" : "keywo",
					    "matching_type" : "conjunction"
		        },
		        {
		                "operator_id" : "__lid1p",
						"operator_type" : "Projection",
						"attributes" : "a,b"
		        },
		        {
		                "operator_id" : "__lid1s",
						"operator_type" : "ScanSource",
						"data_source" : "t2",
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1s",
		                "to" : "__lid1p"
		        },
		        {
		                "from" : "__lid1p",
		                "to" : "__lid0e"
		        },
		        {
		                "from" : "__lid0e",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS SELECT a,b EXTRACT KEYWORDMATCH(b,"key", substring) FROM t; CREATE VIEW t AS EXTRACT KEYWORDMATCH(a,"key", substring) FROM t2;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordMatch",
						"attributes" : "b"
					    "keyword" : "keywo",
					    "matching_type" : "conjunction"
		        },
		        {
		                "operator_id" : "__lid0p",
						"operator_type" : "Projection",
						"attributes" : "a,b"
		        },
		        {
		                "operator_id" : "__lid1e",
						"operator_type" : "KeywordSource",
						"attributes" : "a,b"
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t2"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1e",
		                "to" : "__lid0e"
		        },
		        {
		                "from" : "__lid0e",
		                "to" : "__lid0p"
		        },
		        {
		                "from" : "__lid0p",
		                "to" : "out"
		        }
	        ]
	}
	2. CREATE VIEW out AS SELECT a,b EXTRACT KEYWORDMATCH(b,"key", substring) FROM t; CREATE VIEW t AS SELECT a,b,c EXTRACT KEYWORDMATCH(a,"key", substring) FROM t2;
	{
	        "operators" : [
		        {
		                "operator_id" : "__lid0e",
						"operator_type" : "KeywordMatch",
						"attributes" : "b"
					    "keyword" : "keywo",
					    "matching_type" : "conjunction"
		        },
		        {
		                "operator_id" : "__lid0p",
						"operator_type" : "Projection",
						"attributes" : "a,b"
		        },
		        {
		                "operator_id" : "__lid1p",
						"operator_type" : "Projection",
						"attributes" : "a,b,c"
		        },
		        {
		                "operator_id" : "__lid1e",
						"operator_type" : "KeywordSource",
						"attributes" : "a,b"
					    "keyword" : "key",
					    "matching_type" : "substring",
						"data_source" : "t2"
		        },
		        {
		                "operator_id" : "out",
		                "operator_type" : "FileSink",
						"file_path" : "out.txt"
		        }
	        ],
	        "links" : [
		        {
		                "from" : "__lid1e",
		                "to" : "__lid1p"
		        },
		        {
		                "from" : "__lid1p",
		                "to" : "__lid0e"
		        },
		        {
		                "from" : "__lid0e",
		                "to" : "__lid0p"
		        },
		        {
		                "from" : "__lid0p",
		                "to" : "out"
		        }
	        ]
	}
	3. CREATE VIEW out AS SELECT * FROM x LIMIT 7 OFFSET 3; CREATE VIEW v0 AS SELECT * FROM x LIMIT 15 OFFSET 1;
	limit a,b= min a,b
	offset a,b = sum a,b
	3. CREATE VIEW out AS SELECT * FROM y LIMIT 15 OFFSET 1; CREATE VIEW v0 AS SELECT * FROM x LIMIT 7 OFFSET 3;
	3. CREATE VIEW out AS SELECT * FROM y LIMIT 16 OFFSET 4; CREATE VIEW v1 AS SELECT * FROM v0 LIMIT 5 OFFSET 3; CREATE VIEW v0 AS SELECT * FROM x LIMIT 3 OFFSET 7;
}
*/
    
}
