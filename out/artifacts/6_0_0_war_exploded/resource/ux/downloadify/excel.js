(function(){
	if(typeof Object.keys != 'function') {
		Object.keys = function(object) {
	        var keys = [], property;
	        for (property in object) {
	            if (object.hasOwnProperty(property)) {
	                keys.push(property);
	            }
	        }
	        return keys;
	    };
	}
	if(typeof StringBuffer === 'undefined') {
		StringBuffer = function() {
			this.data = [];
		}
		StringBuffer.prototype.append = function(s) {
			var m = this;
			if(s instanceof StringBuffer) {
				for(var i = 0,l = s.data.length;i < l;i++ ) {
					m.data.push(s.data[i]);
				}
			} else
				m.data.push(s);
			return m;
		}
		StringBuffer.prototype.toString = function() {
			return this.data.join("");
		};
	}
	if(typeof $excel === 'undefined') {
		$excel = function(){
			var self = this, config = function(cfg){
				if(cfg) {
					for(var p in cfg) {
						self[p] = cfg[p];
						if(p == 'headers')
							self.keys = Object.keys(self[p]);
					}
				}
				if(self.keys) {
					generalRowTpl();
				}
				return self;
			}, generalRowTpl = function() {
				var tpl = new StringBuffer().append('<Row>'), reg = {}, exp = [], g;
				for(var i in self.keys) {
					k = self.keys[i];
					t = self.types[k];
					p = 'String';
					y = 'Cell';
					g = '!' + (k == '?' ? '#' : k) + '!';// error char ?
					if(t == 'yyyy-m-d') {
						y = 'Date';
						p = 'DateTime';
					} else if(t == 'yyyy-m-d hh:MM:ss') {
						y = 'DateTime';
						p = 'DateTime';
					} else if(t && t.substr(0, 1) == '0') {
						p = 'Number';
					}
					tpl.append('<Cell ss:StyleID="' + y + '"><Data ss:Type="' + p + '">' + g + '</Data></Cell>');
					exp.push(g.replace(/\(|\)|\\|\^|\$|\[|\]|\.|\*|\+|\?|\||\<|\>|\-|\&/g, function(m){
						return m == '?' ? '#' : ('\\' + m);
					}));
					reg[g] = k;
				}
				self.rowTpl = tpl.append('</Row>').toString();
				self.rowReg = reg;
				self.rowExp = new RegExp(exp.join('|'), 'g');
			};
			config({
				headers: [],
				widths: [],
				types: [],
				locks: [],
				combos: [],
				comboMaps: {},
				sheets: 0,
				rowCount: 0,
				rowTpl: '',
				rowReg: {},
				rowExp: '',
				xml: new StringBuffer()
			});
			config(arguments[0]);
			return self;
		};
		$excel.prototype.clear = function() {
			var self = this;
			delete self.xml;
		};
		$excel.prototype.create = function() {
			var self = this;
			var url = URL.createObjectURL(self.getBlob());
			self.clear();
			return url;
		};
		$excel.prototype.getBlob = function() {
			return new Blob([this.getXml().toString()], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"});
		};
		$excel.prototype.getXml = function() {
			var self = this;
			if(self.sheets > 0)
				self.createOptions();
			return new StringBuffer().append('\ufeff<?xml version="1.0" encoding="UTF-8" standalone="yes"?>')
			.append('<?mso-application progid="Excel.Sheet"?>')
			.append('<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns:html="http://www.w3.org/TR/REC-html40">')
			.append('<DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">')
			.append('<Version>12.00</Version>')
			.append('</DocumentProperties>')
			.append('<ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">')
			.append('<WindowHeight>7524</WindowHeight>')
			.append('<WindowWidth>17280</WindowWidth>')
			.append('<WindowTopX>360</WindowTopX>')
			.append('<WindowTopY>276</WindowTopY>')
			.append('<ActiveSheet>1</ActiveSheet>')
			.append('<ProtectStructure>False</ProtectStructure>')
			.append('<ProtectWindows>False</ProtectWindows>')
			.append('</ExcelWorkbook>')
			.append('<Styles>')
			.append('<Style ss:ID="Default" ss:Name="Normal">')
			.append('<Alignment ss:Vertical="Bottom"/>')
			.append('<Borders />')
			.append('<Font ss:FontName="Arial" x:Family="Swiss"/>')
			.append('<Interior />')
			.append('<NumberFormat />')
			.append('<Protection />')
			.append('</Style>')
			.append('<Style ss:ID="HeaderCell">')
			.append('<Alignment ss:Horizontal="Center" />')
			.append('<Borders>')
			.append('<Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>')
			.append('<Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>')
			.append('<Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>')
			.append('<Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="#000000"/>')
			.append('</Borders>')
			.append('<Font ss:FontName="宋体" x:CharSet="134" x:Family="Roman" ss:Size="12" ss:Bold="1" />')
			.append('<Interior ss:Color="#99CCFF" ss:Pattern="Solid" />')
			.append('</Style>')
			.append('<Style ss:ID="Cell">')
			.append('<Interior />')
			.append('</Style>')
			.append('<Style ss:ID="Date">')
			.append('<Alignment ss:Horizontal="Right" />')
			.append('<NumberFormat ss:Format="yyyy\-mm\-dd" />')
			.append('</Style>')
			.append('<Style ss:ID="DateTime">')
			.append('<Alignment ss:Horizontal="Right" />')
			.append('<NumberFormat ss:Format="yyyy\-mm\-dd hh:MM:ss" />')
			.append('</Style>')
			.append('<Style ss:ID="Integer">')
			.append('<NumberFormat ss:Format="0" />')
			.append('</Style>')
			.append('<Style ss:ID="Float">')
			.append('<NumberFormat ss:Format="0.00" />')
			.append('</Style>')
			.append('</Styles>')
			.append(self.xml)
			.append('</Workbook>');
		};
		$excel.prototype.createSheet = function(count) {
			var self = this;
			if(self.sheets > 0)
				self.createOptions();
			self.append('<Worksheet ss:Name="Sheet' + (++self.sheets) + '">');
			self.append('<Table ss:ExpandedColumnCount="' + self.keys.length + '" ss:ExpandedRowCount="' + (count <= 65534 ? (count + 1) : 
				((count + self.sheets) > (65535*self.sheets) ? 65535 : (count + self.sheets - 65535*(self.sheets-1)))) + 
					'" x:FullColumns="1" x:FullRows="1" ss:DefaultColumnWidth="54" ss:DefaultRowHeight="15.6">');
			self.createHeader();
			return self;
		};
		$excel.prototype.createHeader = function() {
			var self = this, i = 0;
			for(var k in self.headers) {
				self.append('<Column ss:Index="' + (++i) + '" ss:AutoFitWidth="0" ss:Width="' + self.widths[k] + '" />');
			}
			self.append('<Row>');
			for(var k in self.headers) {
				self.append('<Cell ss:StyleID="HeaderCell"><Data ss:Type="String">' + self.headers[k] + '</Data></Cell>');
			}
			return self.append('</Row>');
		};
		$excel.prototype.parseComboValue = function(k, v) {
			var self = this, s = self.comboMaps[k + v];
			if (s)
				return s;
			else
				s = v;
			if(self.combos) {
				var c;
				for(var i in self.combos) {
					c = self.combos[i];
					if(k == c.dlc_fieldname && v == c.dlc_display) {
						s = c.dlc_value;
						break;
					}
				}
			}
			self.comboMaps[k + v] = s;
			return s;
		};
		$excel.prototype.createRow = function(row) {
			var self = this, k, t, v, s = self.rowTpl.replace(self.rowExp, function(match){
				k = self.rowReg[match];
				t = self.types[k];
				if(k) {
					v = row[k];
					if(typeof v != 'undefined' && v != null) {
						if(t == 'yn') {
							v = [1, -1, '1', '-1'].indexOf(v) > -1 ? '是' : (0 == v ? '否' : '');
						} else if(t == 'combo') {
							v = self.parseComboValue(k, v);
						} else if(t == 'yyyy-m-d') {
							v = v.substr(0, 10);
						}
					} else
						v = '';
				} else
					v = '';
				return v;
			});
			// empty datefield
			return self.append(s.replace(/\<Cell\sss:StyleID="Date"\>\<Data\sss:Type="DateTime"\>\<\/Data\>\<\/Cell\>/g, '<Cell ss:StyleID="DateTime"/>'));
		};
		$excel.prototype.addData = function(data) {
			var self = this;
			for(var i = 0,l = data.length;i < l;i++ ) {
				if(self.rowCount == 0 || self.rowCount >= 65534) {
					self.rowCount = 0;
					self.createSheet(l);
				}
				self.createRow(data[i]);
				self.rowCount += 1;
			}
			return self;
		};
		$excel.prototype.append = function(s) {
			this.xml.append(s);
			return this;
		};
		$excel.prototype.createOptions = function() {
			return this.append('</Table>')
			.append('<WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">')
			.append('<Print>')
			.append('<ValidPrinterInfo />')
			.append('<HorizontalResolution>600</HorizontalResolution>')
			.append('<VerticalResolution>600</VerticalResolution>')
			.append('</Print>')
			.append('<Selected />')
			.append('<FreezePanes />')
			.append('<FrozenNoSplit />')
			.append('<SplitHorizontal>1</SplitHorizontal>')
			.append('<TopRowBottomPane>1</TopRowBottomPane>')
			.append('<ProtectObjects>False</ProtectObjects>')
			.append('<ProtectScenarios>False</ProtectScenarios>')
			.append('</WorksheetOptions>')
			.append('</Worksheet>');
		};
	}
})(window);