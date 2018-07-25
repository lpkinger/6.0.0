// JavaScript Document
/**
 * allows for downloading of grid data (store) directly into excel
 * Method: extracts data of gridPanel store, uses columnModel to construct XML excel document,
 * converts to Base64, then loads everything into a data URL link.
 *
 * @author		Animal		<extjs support team>
 *
 */
/**
 * base64 encode / decode
 *
 * @location 	http://www.webtoolkit.info/
 *
 */
var Base64 = (function() {
    // Private property
    var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    // Private method for UTF-8 encoding
    function utf8Encode(string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";
        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);
            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
        }
        return utftext;
    }
    // Public method for encoding
    return {
        encode : (typeof btoa == 'function') ? function(input) {
            return btoa(utf8Encode(input));
        } : function (input) {
            var output = "";
            var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
            var i = 0;
            input = utf8Encode(input);
            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }
                output = output +
                keyStr.charAt(enc1) + keyStr.charAt(enc2) +
                keyStr.charAt(enc3) + keyStr.charAt(enc4);
            }
            return output;
        }
    };
})();
Ext.override(Ext.grid.GridPanel, {
    getExcelXml: function(title, includeHidden) {
        var worksheet = this.createWorksheet(title, includeHidden);
        return '<?xml version="1.0" encoding="utf-8"?>' +
        '<ss:Workbook xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns:o="urn:schemas-microsoft-com:office:office">' +
        '<o:DocumentProperties><o:Title>' + '123' + '</o:Title></o:DocumentProperties>' +
        '<ss:ExcelWorkbook>' +
            '<ss:WindowHeight>' + worksheet.height + '</ss:WindowHeight>' +
            '<ss:WindowWidth>' + worksheet.width + '</ss:WindowWidth>' +
            '<ss:ProtectStructure>False</ss:ProtectStructure>' +
            '<ss:ProtectWindows>False</ss:ProtectWindows>' +
        '</ss:ExcelWorkbook>' +
        '<ss:Styles>' +
            '<ss:Style ss:ID="Default">' +
                '<ss:Alignment ss:Vertical="Top" ss:WrapText="1" />' +
                '<ss:Font ss:FontName="arial" ss:Size="10" />' +
                '<ss:Borders>' +
                    '<ss:Border ss:Color="#e4e4e4" ss:Weight="1" ss:LineStyle="Continuous" ss:Position="Top" />' +
                    '<ss:Border ss:Color="#e4e4e4" ss:Weight="1" ss:LineStyle="Continuous" ss:Position="Bottom" />' +
                    '<ss:Border ss:Color="#e4e4e4" ss:Weight="1" ss:LineStyle="Continuous" ss:Position="Left" />' +
                    '<ss:Border ss:Color="#e4e4e4" ss:Weight="1" ss:LineStyle="Continuous" ss:Position="Right" />' +
                '</ss:Borders>' +
                '<ss:Interior />' +
                '<ss:NumberFormat />' +
                '<ss:Protection />' +
            '</ss:Style>' +
            '<ss:Style ss:ID="title">' +
                '<ss:Borders />' +
                '<ss:Font />' +
                '<ss:Alignment ss:WrapText="1" ss:Vertical="Center" ss:Horizontal="Center" />' +
                '<ss:NumberFormat ss:Format="@" />' +
            '</ss:Style>' +
            '<ss:Style ss:ID="headercell">' +
                '<ss:Font ss:Bold="1" ss:Size="10" />' +
                '<ss:Alignment ss:WrapText="1" ss:Horizontal="Center" />' +
                '<ss:Interior ss:Pattern="Solid" ss:Color="#A3C9F1" />' +
            '</ss:Style>' +
            '<ss:Style ss:ID="even">' +
                '<ss:Interior ss:Pattern="Solid" ss:Color="#CCFFFF" />' +
            '</ss:Style>' +
            '<ss:Style ss:Parent="even" ss:ID="evendate">' +
            '<ss:NumberFormat ss:Format="yyyy-MM-dd" />' +
            '</ss:Style>' +
            '<ss:Style ss:Parent="even" ss:ID="evenint">' +
                '<ss:NumberFormat ss:Format="0" />' +
            '</ss:Style>' +
            '<ss:Style ss:Parent="even" ss:ID="evenfloat">' +
                '<ss:NumberFormat ss:Format="0.00" />' +
            '</ss:Style>' +
            '<ss:Style ss:ID="odd">' +
                '<ss:Interior ss:Pattern="Solid" ss:Color="#CCCCFF" />' +
            '</ss:Style>' +
            '<ss:Style ss:Parent="odd" ss:ID="odddate">' +
                '<ss:NumberFormat ss:Format="yyyy-MM-dd" />' +
            '</ss:Style>' +
            '<ss:Style ss:Parent="odd" ss:ID="oddint">' +
                '<ss:NumberFormat ss:Format="0" />' +
            '</ss:Style>' +
            '<ss:Style ss:Parent="odd" ss:ID="oddfloat">' +
                '<ss:NumberFormat ss:Format="0.00" />' +
            '</ss:Style>' +
        '</ss:Styles>' +
        worksheet.xml +
        '</ss:Workbook>';
    },
    createWorksheet: function(title, includeHidden) {
        var cellType = [];
        var cellTypeClass = [];
        var cm = this.columns;
        var totalWidthInPixels = 0;
        var colXml = '';
        var headerXml = '';
        var visibleColumnCountReduction = 0;
        var colCount = cm.length;
        for (var i = 0; i < colCount; i++) {
        	cm[i].header = cm[i].header == null ? cm[i].text : cm[i].header;
            if ((cm[i].dataIndex != '') && (includeHidden || cm[i].width != 0)) {
                var w = cm[i].width;
                totalWidthInPixels += w;
                if (cm[i].header === ""){
                	cellType.push("None");
                	cellTypeClass.push("");
                	++visibleColumnCountReduction;
                }
                else
                {
                    colXml += '<ss:Column ss:AutoFitWidth="1" ss:Width="' + w + '" />';
                    headerXml += '<ss:Cell ss:StyleID="headercell">' +
                        '<ss:Data ss:Type="String">' + cm[i].header + '</ss:Data>' +
                        '<ss:NamedCell ss:Name="Print_Titles" /></ss:Cell>';
                    if(this.store.fields){
                    	var fld = this.store.fields.get(cm[i].dataIndex);
                        switch(fld.type) {
                            case "int":
                                cellType.push("Number");
                                cellTypeClass.push("int");
                                break;
                            case "float":
                                cellType.push("Number");
                                cellTypeClass.push("float");
                                break;
                            case "bool":
                            case "boolean":
                                cellType.push("String");
                                cellTypeClass.push("");
                                break;
                            case "date":
                                /*cellType.push("DateTime");
                                cellTypeClass.push("date");*/
                            	  cellType.push("String");
                                  cellTypeClass.push("");
                                break;
                            default:
                                cellType.push("String");
                                cellTypeClass.push("");
                                break;
                        }
                    } else {
                    	switch(cm[i].xtype) {
                        case "numbercolumn":
                            cellType.push("Number");
                            cellTypeClass.push("float");
                            break;
                        case "booleancolumn":
                            cellType.push("String");
                            cellTypeClass.push("");
                            break;
                        case "datecolumn":
                            /*cellType.push("DateTime");
                            cellTypeClass.push("");*/
                        	  cellType.push("String");
                              cellTypeClass.push("");
                            break;
                        default:
                            cellType.push("String");
                            cellTypeClass.push("");
                            break;
                    	}
                    }
                }
            }
        }
        var visibleColumnCount = cellType.length - visibleColumnCountReduction;
        var result = {
            height: 9000,
            width: Math.floor(totalWidthInPixels * 30) + 50
        };
        if(!title){
        	title = "优软ERP系统导出数据";
        }
        var t = '<ss:Worksheet ss:Name="' + title + '">' +
            '<ss:Names>' +
            '<ss:NamedRange ss:Name="Print_Titles" ss:RefersTo="=\'' + title+ '\'!R1:R2" />' +
            '</ss:Names>' +
            '<ss:Table x:FullRows="1" x:FullColumns="1"' +
            ' ss:ExpandedColumnCount="' + (visibleColumnCount + 2) +
            '" ss:ExpandedRowCount="' + (this.store.getCount() + 2) + '">' +
            colXml +
            '<ss:Row ss:Height="38">' +
            '<ss:Cell ss:StyleID="title" ss:MergeAcross="' + (visibleColumnCount - 1) + '">' +
            '<ss:Data xmlns:html="http://www.w3.org/TR/REC-html40" ss:Type="String">' +
            '<html:B>' + title + '</html:B></ss:Data><ss:NamedCell ss:Name="Print_Titles" />' +
            '</ss:Cell>' +
            '</ss:Row>' +
            '<ss:Row ss:AutoFitHeight="1">' +
            headerXml +
            '</ss:Row>';
        for (var i = 0, it = this.store.data.items, l = it.length; i < l; i++) {
            t += '<ss:Row>';
            var cellClass = (i & 1) ? 'odd' : 'even';
            r = it[i].data;
            var k = 0;
            for (var j = 0; j < colCount; j++) {
                if ((cm[j].dataIndex != '')
                    && (includeHidden || cm[j].width != 0)) {
                    var v = r[cm[j].dataIndex];
                    if (cellType[k] !== "None") {
                        t += '<ss:Cell ss:StyleID="' + cellClass + cellTypeClass[k] + '"><ss:Data ss:Type="' + cellType[k] + '">';
                        if (cm[j].xtype == 'datecolumn') {
                        	if(v != null && v != '' && !v.toString().match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/)){
                        		t += Ext.Date.toString(new Date(v));//v.format('Y-m-d');
                        	} else {
                        		if(v==null){
                        			v='';
                        		}
                        		t += v;
                        	}
                        } else {
                            t += v;
                        }
                        t +='</ss:Data></ss:Cell>';
                    }
                    k++;
                }
            }
            t += '</ss:Row>';
        }
        result.xml = t + '</ss:Table>' +
        '<x:WorksheetOptions>' +
            '<x:PageSetup>' +
                '<x:Layout x:CenterHorizontal="1" x:Orientation="Landscape" />' +
                '<x:Footer x:Data="Page &amp;P of &amp;N" x:Margin="0.5" />' +
                '<x:PageMargins x:Top="0.5" x:Right="0.5" x:Left="0.5" x:Bottom="0.8" />' +
            '</x:PageSetup>' +
            '<x:FitToPage />' +
            '<x:Print>' +
                '<x:PrintErrors>Blank</x:PrintErrors>' +
                '<x:FitWidth>1</x:FitWidth>' +
                '<x:FitHeight>32767</x:FitHeight>' +
                '<x:ValidPrinterInfo />' +
                '<x:VerticalResolution>600</x:VerticalResolution>' +
            '</x:Print>' +
            '<x:Selected />' +
            '<x:DoNotDisplayGridlines />' +
            '<x:ProtectObjects>False</x:ProtectObjects>' +
            '<x:ProtectScenarios>False</x:ProtectScenarios>' +
        '</x:WorksheetOptions>' +
    '</ss:Worksheet>';
     return result;
    }
});
