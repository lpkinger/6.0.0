/**
 * 杂七杂八的方法
 * @author yingp
 */
Ext.define('erp.util.BaseUtil',{
	getSequenceId : function(seqname){
		Ext.Ajax.request({
	   		url : basePath + 'common/getCodeString.action',
	   		async: false,//同步ajax请求
	   		params: {
	   			caller: caller,//如果table==null，则根据caller去form表取对应table
	   			table: table,
	   			type: type
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);//Ext.decode():解码(解析)json字符串对象
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				Ext.getCmp(codeField).setValue(localJson.code);//getCmp():通过id查找现有的Component
	   			}
	   		}
		});
	},
	
	/**
	 * 取编号
	 * @param table 表名
	 * @param type 
	 * @param codeField 编号字段
	 */
	getRandomNumber: function(table, type, codeField){
		var form = Ext.getCmp('form');
		if(form){
		table = table == null ? form.tablename : table;
		}
		type = type == null ? 2 : type;
		codeField = codeField == null ? form.codeField : codeField;
		Ext.Ajax.request({
	   		url : basePath + 'common/getCodeString.action',
	   		async: false,//同步ajax请求
	   		params: {
	   			caller: caller,//如果table==null，则根据caller去form表取对应table
	   			table: table,
	   			type: type
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				Ext.getCmp(codeField).setValue(localJson.code);
	   			}
	   		}
		});
	},
	/**
	 * 解析url,获得传递的参数
	 */
	getUrlParam: function(name){
		var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");
	    var r=window.location.search.substr(1).match(reg);
	    if(r!=null)
	    	return decodeURI(r[2]);
	    return null;
	},
	/**
	 * 获取配置参数值
	 */
	getSetting : function(caller, code,callback,async) {
	   async=async==undefined?true:async;
 	   Ext.Ajax.request({
 		   url : basePath + 'ma/setting/config.action?caller=' + caller + '&code=' + code,
 		   method : 'GET',
 		   async:async,
 		   callback : function(opt, s, res){
 			   var val = null;
 			   if(res && res.responseText){
 				  var r = new Ext.decode(res.responseText);
 	 			   if(r && r.exceptionInfo){
 	 				   showError(r.exceptionInfo);return;
 	 			   } else {
 	 				   val = r.data;
 	 				   if(val) {
 	 					  switch(r.data_type) {
 	 					  case 'YN':
 	 						  val = val == '1';break;
 	 					  case 'NUMBER':
 	 						  val = Number(val);break;
 	 					  }
 	 					  if(r.multi == 1)
 	 						  val = val.split('\n');
 	 				   }
 	 			   } 
 			   }
 			  callback && callback.call(null, val);
 		   }
 	   });
    },
	/**
	 * string:原始字符串
	 * substr:子字符串
	 * isIgnoreCase:忽略大小写
	 */
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},
    getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	return tab;
	},
	/**
	 * 文件下载
	 * @param path 文件路径
	 */
	download: function(path){
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/download.action',
	   		params: {
	   			path: path
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				if(me.contains(path, "\\", true)){
    					showMessage("温馨提示", path.substring(path.lastIndexOf("\\") + 1) + "已保存成功");
    				} else {
    					showMessage("温馨提示", path.substring(path.lastIndexOf("/") + 1) + "已保存成功");
    				}
	   			}
	   		}
		});
	},
	/**
	 * js生成excel的xml
	 * 只导出当前Store的数据
	 * 只能wps打开
	 */
	exportexcel: function(panel, title){
		var me = this;
		title = title == null ? me.getActiveTab().tabConfig.tooltip : title;
		var excelxml = panel.getExcelXml(title);
		var fd = Ext.get('frmDummy');
        if (!fd) {
            fd = Ext.DomHelper.append(
                    Ext.getBody(), {
                        tag : 'form',
                        method : 'post',
                        id : 'frmDummy',
                        action : basePath + 'jsps/common/excel.jsp',
                        target : '_blank',
                        name : 'frmDummy',
                        cls : 'x-hidden',
                        cn : [ {
                            tag : 'input',
                            name : 'content',
                            id : 'content',
                            type : 'hidden'
                        } , {
                            tag : 'input',
                            name : 'file',
                            id : 'file',
                            type : 'hidden'
                        } ]
                    }, true);
            
        }
        fd.child('#file').set({
            value : title + new Date().getTime()
        });
        fd.child('#content').set({
            value : excelxml
        });
        fd.dom.submit();
	},
	/**
	 * 导出excel和pdf的请求使用这个方法获取title
	 * 如果form配置的fo_exportitle进行了配置，则文件名为title+fo_exportitle
	 * @param {} title
	 * @return {}
	 */
	pageTitle: function(title) {
		var titles = "";
		/**
		 * @author lidy
		 * 判断fo_exportitle是否配置
		 */
		if(typeof(caller)!=='undefined'&&caller){
			Ext.Ajax.request({
			   url : basePath + '/common/getFieldData.action',
			   async: false,
			   params: {
				   caller: 'Form',
				   field: 'fo_exportitle',
				   condition: 'fo_caller=\'' + caller + '\''
			   },
			   method : 'post',
			   callback : function(opt, s, res){
				   var r = new Ext.decode(res.responseText);
				   if (r.success && r.data) {
						var fo_exportitle = r.data;
						if(fo_exportitle && fo_exportitle.trim() != ""){
							var exportitles = fo_exportitle.split("#");
							Ext.each(exportitles , function(value,index){
								var t = Ext.getCmp(value.trim());
								if(t&&t.value!=""){
									titles = titles + "_" + t.value;
								}
							})
						}
				   }
			   }
			});
		}
		if(titles == ""){
			return title||(this.getActiveTab().title || this.getActiveTab().tabConfig.tooltip) + Ext.Date.format(new Date(), 'Y-m-d H:i:s');
		}else{
			return (title||this.getActiveTab().title || this.getActiveTab().tabConfig.tooltip) + titles;
		}
	},
	/**
	 * Java ApachePoi 生成excel
	 * 导出当前Caller对应数据库全部Store
	 * @param caller 
	 * @param type datalist、detailgrid
	 * @param condition 条件
	 */
	createExcel: function(caller, type, condition, title, remark, customFields, grid){
		condition = condition == null ? '' : condition;
		title = this.pageTitle(title);
		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		} 
		var bool = true, _noc = this.getUrlParam("_noc"), lg = 0;// lg = 1表示大数据
		_noc = _noc || (type == 'detailgrid' ? 1 : (grid ? grid._noc : 0));
		Ext.Ajax.request({
			url: basePath + 'common/beforeExport.action',
			params: {
				caller: caller,
				type: type,
				_self:getUrlParam('_self'),
				condition: condition
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
					bool = false;
				} else if(rs.busy) {
					showMessage('下载提示', '<h1>需要导出数据的人过多</h1>请稍后再试...');
					bool = false;
				} else {
					 if(rs.count > 100000) {
						 showMessage('下载提示', '<h1>数据量过大</h1>当前总数据为' + rs.count + '条，超过导出上限(10万条)，系统将为您导出前10万条<br>请稍等...');
					 }
					 lg = rs.count > 5000 ? 1 : 0;
				}
			}
		});
		if(!bool) return;
		if(remark){
			Ext.Ajax.request({
				url: basePath + 'common/excel/gridWithRemark.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					caller: caller,
					type: type,
					title: unescape(title.replace(/\\/g,"%").replace(/,/g," ")),
					condition: condition,
					remark: unescape(remark.replace(/\\/g,"%")),
					fields : customFields,
					_noc: _noc,
					lg: lg
				}
			});
		} else {
			Ext.Ajax.request({
				url: basePath + 'common/excel/create.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					caller: caller,
					type: type,
					title: unescape(title),
					condition: condition,
					fields : customFields,
					_noc: _noc,
					_self:getUrlParam('_self'),
					lg: lg
				}
			});
		}
	},
	/**
	 * Java AppachePoi 生成excel
	 * 导出当前Grid的Store
	 * @param grid
	 * @param title xls文件名
	 * @param noSummary 1 不导出合计行  否则 按配置导出
	 */
	exportGrid: function(grid, title, remark,noSummary){
		title = this.pageTitle(title);
		var columns = (grid.columns && grid.columns.length > 0) ? 
				grid.columns : grid.headerCt.getGridColumns(),
				cm = new Array(), datas = new Array(), gf = grid.store.groupField;
		Ext.Array.each(columns, function(c){
			if(c.dataIndex == gf || (!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd)) {
				if((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
					var items = (c.items && c.items.items) || c.columns;
					Ext.Array.each(items, function(item){
						if(!item.hidden) {
							var text = (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')) + '(' + item.text.replace(/<br>/g, '\n') + ')';
							cm.push({
								text: text, 
								dataIndex: item.dataIndex, 
								width: item.width, 
								xtype: item.xtype, 
								format: item.format, 
								locked: item.locked, 
								summary: noSummary==1 ? false : item.summaryType == 'sum',
								group: item.dataIndex == gf,
								logic: item.logic
							});		
						}
					});
				} else {
					// ext4.2 GroupHeader
					var text = ((c.ownerCt && c.ownerCt.isGroupHeader ? '(' +c.ownerCt.text + ')' : '') + (c.text || '')).replace(/<br>/g, '\n');
					//2018040472 , 导出模版时，清除text存在的勾选框代码
					text = text.replace(/<.*?\/>/,'');
					cm.push({
						text: text, 
						dataIndex: c.dataIndex, 
						width: (c.dataIndex == gf ? 100 : c.width), 
						xtype: c.xtype, format: c.format, 
						locked: c.locked, 
						summary: noSummary==1 ? false : c.summaryType == 'sum',
						group: c.dataIndex == gf,
						logic: c.logic
					});
				}
			}
		});
		if(grid.store.tree) {//TreeGrid
			var root = grid.store.tree.root;
			var pf = function(node) {
				if(node) {
					var dd = node.data, keys = Ext.Object.getKeys(dd);
					Ext.each(keys, function(k){
						var v = dd[k];
						if(v == null) {
							dd[k] = '';
						} else if(Ext.isDate(v)){
							dd[k] = Ext.Date.format(v, 'Y-m-d');
						} else if(Ext.isNumber(v)){
							dd[k] = String(v);
						}
					});
					datas.push(dd);
					if(node.childNodes.length > 0) {
						Ext.each(node.childNodes, function(n) {
							pf(n);
						});
					}
				}
			};
			Ext.each(root.childNodes, function(n) {
				pf(n);
			});
		} else {
			var store = grid.getView().getStore(),
				items = store.data.items;
			if(store.buffered) {
				items = store.prefetchData.items;
			}
			var numreg = /^(-?\d+)(\.\d+)?$/, datereg = /\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/,strreg = /["]/;
			var pf = function(c, dd, ss) {
				if(c.xtype == 'datecolumn'){
					c.format = c.format || 'Y-m-d';
					if(Ext.isDate(dd[c.dataIndex])){
						ss[c.dataIndex] = Ext.Date.format(dd[c.dataIndex], c.format);
					} else if(datereg.test(dd[c.dataIndex])) {
						ss[c.dataIndex] = Ext.Date.format(
								Ext.Date.parse(dd[c.dataIndex], 'Y-m-d H:i:s'), c.format);
					}
				} else if(c.xtype == 'datetimecolumn'){
					if(Ext.isDate(dd[c.dataIndex])){
						ss[c.dataIndex] = Ext.Date.format(dd[c.dataIndex], 'Y-m-d H:i:s');
					} else if(datereg.test(dd[c.dataIndex])) {
						ss[c.dataIndex] = dd[c.dataIndex];
					}
				} else if(c.xtype == 'numbercolumn'){
					if(Ext.isNumber(dd[c.dataIndex])){
						ss[c.dataIndex] = String(dd[c.dataIndex].toString());
					} else if(numreg.test(dd[c.dataIndex])){
						ss[c.dataIndex] = String(dd[c.dataIndex].toString());
					} else {
						ss[c.dataIndex] = '0';
					}
				} else if(c.xtype == 'yncolumn') {
					ss[c.dataIndex] = dd[c.dataIndex] == 0 ? '否' : '是';
				} else {				
					ss[c.dataIndex] = dd[c.dataIndex] ?  String(dd[c.dataIndex]).replace(strreg,'') : '';
				}
				if(ss[c.dataIndex] == null) {
					ss[c.dataIndex] = '';
				}
			};
			Ext.each(items, function(d){
				var ss = {};
				Ext.each(columns, function(c){
					if(c.dataIndex == gf || (!c.hidden && (c.width > 0 || c.flex>0)&& !c.isCheckerHd)) {
						if((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
							var items = ( c.items && c.items.items) || c.columns;
							Ext.Array.each(items, function(item){
								if(!item.hidden) {
									pf(item, d.data, ss);
								}
							});
						} else {
							pf(c, d.data, ss);
						}
					}
				});
				datas.push(ss);
			});
		}
		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = frm.id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		}  
		if(remark){//抬头上面的注释    注释为第一行
//			remark = remark.replace(/<br>/g,'\n');
			Ext.Ajax.request({
				url: basePath + 'common/excel/gridWithRemark.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					datas: unescape(Ext.JSON.encode(datas).replace(/\\u/g,"%u")),
					columns: Ext.encode(cm),
					title: unescape(title.replace(/\\u/g,"%u").replace(/,/g," ")),
					remark:unescape(remark.replace(/\\u/g,"%u"))
				}
			});
		}else{	//无注释  抬头为第一行
			Ext.Ajax.request({
				url: basePath + 'common/excel/grid.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					datas: unescape(Ext.JSON.encode(datas).replace(/\\u/g,"%u")),
					columns: unescape(Ext.encode(cm).replace(/\\u/g,"%u")),
					title: title
				}
			});
		}
	},
	exportPdf: function(grid, title){
		title = this.pageTitle(title);
		var columns = (grid.columns && grid.columns.length > 0) ? 
				grid.columns : grid.headerCt.getGridColumns(),
				cm = new Array(), datas = new Array();
		Ext.Array.each(columns, function(c){
			if(!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd) {
				if(c.items && c.items.length > 0) {
					var items = c.items.items;
					Ext.Array.each(items, function(item){
						if(!item.hidden)
							cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')) + '(' + item.text.replace(/<br>/g, '\n') + ')', 
								dataIndex: item.dataIndex, width: item.width, xtype: item.xtype, format: item.format});
					});
				} else {
					cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')), dataIndex: c.dataIndex, width: c.width, xtype: c.xtype, format: c.format});
				}
			}
		});
		var items = grid.store.data.items;
		if(grid.store.buffered) {
			items = grid.store.prefetchData.items;
		}
		var datereg = /\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/;
		var pf = function(c, dd, ss) {
			if(c.xtype == 'datecolumn'){
				c.format = c.format || 'Y-m-d';
				if(Ext.isDate(dd[c.dataIndex])){
					ss[c.dataIndex] = Ext.Date.format(dd[c.dataIndex], c.format);
				} else if(datereg.test(dd[c.dataIndex])) {
					ss[c.dataIndex] = Ext.Date.format(
							Ext.Date.parse(dd[c.dataIndex], 'Y-m-d H:i:s'), c.format);
				}
			} else if(c.xtype == 'datetimecolumn'){
				if(Ext.isDate(dd[c.dataIndex])){
					ss[c.dataIndex] = Ext.Date.format(dd[c.dataIndex], 'Y-m-d H:i:s');
				} else if(datereg.test(dd[c.dataIndex])) {
					ss[c.dataIndex] = dd[c.dataIndex];
				}
			} else if(c.xtype == 'numbercolumn'){
				if(Ext.isNumber(dd[c.dataIndex])){
					ss[c.dataIndex] = String(dd[c.dataIndex].toString());
				}
			} else if(c.xtype == 'yncolumn') {
				ss[c.dataIndex] = dd[c.dataIndex] == 0 ? '否' : '是';
			} else {
				ss[c.dataIndex] = dd[c.dataIndex] ?  String(dd[c.dataIndex]) : '';
			}
			if(ss[c.dataIndex] == null) {
				ss[c.dataIndex] = '';
			}
		};
		Ext.each(items, function(d){
			var ss = {};
			Ext.each(columns, function(c){
				if(!c.hidden && c.width > 0 && !c.isCheckerHd) {
					if(c.items && c.items.length > 0) {
						var items = c.items.items;
						Ext.Array.each(items, function(item){
							if(!item.hidden) {
								pf(item, d.data, ss);
							}
						});
					} else {
						pf(c, d.data, ss);
					}
				}
			});
			datas.push(ss);
		});
		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = frm.id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		}  
		Ext.Ajax.request({
			url: basePath + 'common/document/grid.pdf',
			method: 'post',
			form: Ext.fly('ext-grid-excel'),
			isUpload: true,
			params: {
				datas: unescape(Ext.JSON.encode(datas).replace(/\\/g,"%")),
				columns: unescape(Ext.encode(cm).replace(/\\/g,"%")),
				title: title
			}
		});
	},
	/**
	 * Java AppachePoi 生成excel
	 * 导出当前Grid的Store
	 * @param grid
	 * @param title xls文件名
	 */
	customExport: function(cal, grid, title, action, condition, args){
		title = this.pageTitle(title);
		var columns = grid.columns,cm = new Array();
		Ext.Array.each(columns, function(c){
			if(!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd) {
				if(c.items && c.items.length > 0) {
					var items = c.items.items;
					Ext.Array.each(items, function(item){
						if(!item.hidden)
							cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')) + '(' + item.text.replace(/<br>/g, '\n') + ')', 
								dataIndex: item.dataIndex, width: item.width, xtype: item.xtype, format: item.format});
					});
				} else {
					cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')), dataIndex: c.dataIndex, width: c.width, xtype: c.xtype, format: c.format});
				}
			}
		});
		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		}  
		var params = {
			caller : cal,
			condition : condition,
			columns: unescape(Ext.encode(cm).replace(/\\/g,"%")),
			title: title
		};
		Ext.Ajax.request({
			url: basePath + (action || 'common/excel/grid.xls'),
			method: 'post',
			form: Ext.fly('ext-grid-excel'),
			isUpload: true,
			params: Ext.apply(params, args)
		});
	},
	/**
	 * 跳转到add页面
	 */
	onAdd: function(panelId, title, url){
		var main = parent.Ext.getCmp("content-panel") || parent.parent.Ext.getCmp("content-panel");
    	if(main){
    		panelId = panelId == main.getActiveTab().id ? Math.random() : panelId;
    		var panel = Ext.getCmp(panelId || Math.random());
    		if(!panel){ 
        		var value = "";
    	    	if (title.toString().length>5) {
    	    		 value = title.toString().substring(0,5);	
    	    	} else {
    	    		value = title;
    	    	}
    	    	if(!contains(url, 'http://', true) && !contains(url, basePath, true)){
    	    		url = basePath + url;
    	    	}
    	    	panel = { 
    	    			title : value,
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:title},
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab',
    	    			html : '<iframe id="iframe_add_'+panelId+'" src="' + url+'" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
    	    			closable : true
    	    	};
    	    	this.openTab(panel, panelId);
        	} else { 
    	    	main.setActiveTab(panel); 
        	}
    	} else {
    		if(!contains(url, basePath, true)){
	    		url = basePath + url;
	    	}
    		window.open(url);
    	}
	},
	openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    },
    getPaydate : function(paymentmethodidfield,startdatefield,datefield){
    	var paymentmethodid,startdate,startdateString;
    	if(Ext.getCmp(paymentmethodidfield)){
    		paymentmethodid = Ext.getCmp(paymentmethodidfield).getValue();
    	}
    	if(Ext.getCmp(startdatefield)){
    		startdate = Ext.getCmp(startdatefield).getValue();
    		startdateString = Ext.Date.format(startdate,'Y-m-d');
    	}
    	if(paymentmethodid!=null&&paymentmethodid!=''&&startdateString!=null&&startdateString!=''){
    		Ext.Ajax.request({
    	   		url : basePath + 'common/getPayDate.action',
    	   		async: false,//同步ajax请求
    	   		params: {
//    	   			caller: caller,//如果table==null，则根据caller去form表取对应table
    	   			paymentmethodid: paymentmethodid,
    	   			startdateString: startdateString
    	   		},
    	   		method : 'post',
    	   		callback : function(options,success,response){
    	   			var localJson = new Ext.decode(response.responseText);
    	   			if(localJson.exceptionInfo){
    	   				showError(localJson.exceptionInfo);
    	   			}
        			if(localJson.success){
        				if(Ext.getCmp(datefield)){
        					Ext.getCmp(datefield).setValue(localJson.paydate);
        				}
    	   			}
    	   		}
    		});
    	}
    },
    JTStr : "丢并乱亘亚夫伫布占并来仑徇侣局俣系侠表伥俩仓个们幸仿伦伟逼侧侦伪杰伧伞备效家佣偬传伛债伤倾偻仅佥仙侨仆伪侥偾雇价仪侬亿侩俭傧俦侪尽偿优储俪傩傥俨凶兑儿兖内两册胄幂净冻凛凯别删刭则锉克刹刚剥剐剀创铲划札剧刘刽刿剑剂劲动务勋胜劳势绩劢勋励劝匀陶匦汇匮区协昂恤却厍厕厌厉厣参丛吴呐吕啕呙员呗念问启哑启衔唤丧乔单哟呛啬吗呜唢哔叹喽呕啧尝唛哗唠啸叽哓呒恶嘘哒哝哕嗳哙喷吨当咛吓哜噜啮呖咙向喾严嘤啭嗫嚣冁呓罗苏嘱囱囵国围园圆图团丘垭执坚垩埚尧报场块茔垲埘涂冢坞埙尘堑垫坠堕坟垦坛压垒圹垆坏垄坜坝壮壶寿够梦夹奂奥奁夺奖奋妆你姗奸侄娱娄妇娅娲妫媪妈妪妩娴妫娆婵娇嫱嫒嬷嫔婴婶娘娈孙学孪宫寝实宁审写宽宠宝将专寻对导尴届屉屡层屦属冈岘岛峡崃昆岗仑峥嵛岚嵝崭岖崂峤峄嵘岭屿岳岿峦巅巯卺帅师帐带帧帏帼帻帜币帮帱开干几库厕厢厩厦厨庙厂庑废广廪庐厅弑吊弪张强弹弥弯汇彦雕佛径从徕复彻汹恒汹耻悦怅闷凄敦恶恼恽恻爱惬悫怆恺忾栗态愠惨惭恸惯悫怄怂虑悭庆戚欲忧惫怜凭愦惮愤悯怃宪忆恳应怿懔怼懑恹惩懒怀悬忏惧慑恋戋戗戬战戏户抛挟舍扪卷扫抡挣挂采拣扬换挥背损摇捣掏抢捂掴掼搂挚抠抟掺捞撑挠捻挢掸拨抚扑揿挞挝捡拥掳择击挡担据挤拟摈拧搁掷扩撷摆擞撸扰摅撵拢拦撄搀撺携摄攒挛摊搅揽考败叙敌数敛毙斓斩断升时晋昼晕晖畅暂晔历昙晓暧旷晒书会胧术东栅杆栀条枭弃枨枣栋栈栖桠匾杨枫桢业极搌杩荣桤盘构枪连椠椁桨桩乐枞梁楼标枢样朴树桦桡桥机椭横檩柽档桧检樯台槟祢柠槛柜橹榈栉椟橼栎橱槠栌枥橥榇栊榉棂樱栏权榄钦叹欧欤欢岁历归殁残殒殇殚僵殓殡歼杀壳壳肴毁殴毵毡氇气氢氩氲氽泛污决没冲况汹浃泾凉凄泪渌净沦渊涞浅涣减涡测浑凑浈涌汤沩准沟温沧灭涤荥沪滞渗卤浒滚满渔沤汉涟渍涨溆渐浆颍泼洁沩潜润浔溃滗涠涩澄浇涝涧渑泽泶浍淀浊浓湿泞蒙济涛滥潍滨溅泺滤滢渎泻渖浏濒泸沥潇潆潴泷濑弥潋澜沣滠洒漓滩湾滦灾为乌烃无炼炜烟茕焕烦炀荧炝热炽烨灯炖烧烫焖营灿毁烛烩熏烬焘烁炉烂争为爷尔墙牍它牵荦犊牺状狭狈狰犹狲呆狱狮奖独狯猃狞获猎犷兽獭献猕猡珏佩现珐珲玮琐瑶莹玛琅琏玑瑷环玺琼珑璎瓒瓯产产苏亩毕画畲异当畴叠痉麻痹疯疡痪瘗疮疟疗痨痫瘅愈疠瘪痴痒疖症癞癣瘿瘾瘫癫发皑皲皱杯盗盏尽监盘卢汤众困睁睐睾眯瞒了睑蒙胧瞩矫炮朱硖砗砚硕砀确码砖碜碛矶硗础碍礴矿砺砾矾砻他佑秘禄祸祯御禅礼祢祷秃税秆禀扁种称谷稣积颖穑秽稳获窝洼穷窑窭窥窜窍窦灶窃竖竞笔笋笕笺筝节范筑箧笃筛筚箦篓箪简篑箫檐签帘篮筹箨籁笼签篱箩吁粤糁粪粮团粝籴纠纪纣约红纡纥纨纫纹纳纽纾纯纰纱纸级纷纭纺扎细绂绁绅绍绀绋绐绌终弦组绊绗结绝绦绞络绚给绒统丝绛绝绢绑绡绠绨绣绥困经综缍绿绸绻线绶维绾纲网绷缀彩纶绺绮绽绰绫绵绲缁紧绯绿绪缃缄缂线缉缎缔缗缘缌编缓缅纬缑缈练缏缇致萦缙缢缒绉缣缚缜缟缛县绦缝缡缩纵缧纤缦絷缕缥总绩绷缫缪缯织缮缭绕绣缋绳绘系茧缳缲缴绎继缤缱缬纩续缠缨纤缆钵罂坛罚骂罢罗罴羁芈羟羡义习翘专耧圣闻联聪声耸聩聂职聍听聋肃巯胁脉胫脱胀肾脶脑肿脚肠腽嗉肤胶腻胆脍脓脸脐膑腊胪脏卧临台与兴举旧衅铺舱舣舰舻艰艳刍苎兹荆豆庄茎荚苋华苌莱万莴扁叶荭苇药荤莳莅苍荪席盖莲苁荜卜蒌蒋葱茑荫荨蒇荞芸莸荛蒉荡芜萧蓣荟蓟芗姜蔷剃莶荐萨荠蓝荩艺药薮蕴苈蔼蔺蕲芦苏蕴苹藓蔹茏兰萝处虚虏号亏虬蛱蜕蚬蚀虾蜗蛳蚂萤蝼蛰蝈虮蝉蛲虫蛏蚁蝇虿蝎蛴蝾蜡蛎蛊蚕蛮杯众炫术卫冲只衮袅补装里制复裤裢褛亵裥裥袄裣裆褴袜衬袭见规觅视觇觋觎亲觊觏觐觑觉览觌观觞觯触订讣计讯讧讨讦训讪讫托记讹讶讼欣诀讷访设许诉诃诊注证诂诋讵诈诒诏评诎诅词咏诩询诣试诗诧诟诡诠诘话该详诜诙诖诔诛诓夸志认诳诶诞诱诮语诚诫诬误诰诵诲说说谁课谇诽谊调谄谆谈诿请诤诹诼谅论谂谀谍谝喧诨谔谛谐谏谕谘讳谙谌讽诸谚谖诺谋谒谓誊诌谎谜谧谑谡谤谦谥讲谢谣谣谟谪谬讴谨谩哗证谲讥撰谮识谯谭谱噪谵毁译议谴护誉读变谗让谶赞谠溪岂竖丰猪猫贝贞负财贡贫货贩贪贯责贮贳赀贰贵贬买贷贶费贴贻贸贺贲赂赁贿赅资贾贼赈赊宾赇周赉赐赏赔赓贤卖贱赋赕质账赌赖赚赙购赛赜贽赘赠赞赝赡赢赆赃赎赝赣赃赶赵趋迹践逾踊跄跸迹蹒踪糟跷趸踌跻跃踯踬蹑躏躯车轧轨军轩轫轭软轸轴轵轺轲轶轼较辂辁载轾辄挽辅轻辆辎辉辋辍辊辇辈轮辑辏输辐辗舆毂辖辕辘转辙轿辚轰辔办辞辫辩农回乃迳这连周进游运过达违遥逊递远适迟迁选遗辽迈还迩边逻逦郏邮郓乡邹邬郧邓郑邻郸邺郐邝郦腌盏酝丑酝医酱酿衅酽释钆钇钌钊钉钋针钓钐扣钏钒钗钍钕钯钫钭钠钝钩钤钣钞钮钧钙钬钛钪铌铈钶铃钴钹铍钰钸铀钿钾钜铊铉刨铋铂钳铆铅钺钵钩钲钼钽铰铒铬铪银铳铜铣铨铢铭铫衔铑铷铱铟铵铥铕铯铐焊锐销锑锉铝锒锌钡铤铗锋锊锓锄锔锇铺锐铖锆锂铽锯钢锞录锖锩锥锕锟锤锱铮锛锬锭钱锦锚锡锢错录锰表铼钔锴锅镀锷铡锻锸锲锹锾键锶锗针锺镁镑锁镉钨蓥镏铠铩锼镐镇镒镍镓镌镞镟链镆镙镝铿锵镗镘镛铲镜镖镂錾铧镤镪锈铙铴镣铹镦镡钟镫镨镄镌镰镯镭铁环铎铛镱铸鉴鉴铄镳钥镶镊锣钻銮凿长门闩闪闫闭开闶闳闰闲闲间闵闸阂阁阀闺闽阃阆闾阅阅阊阉阎阏阍阈阌阒板闱阔阕阑阗阖阙闯关阚阐辟闼陉陕升阵阴陈陆阳堤陧队阶陨际随险隐陇隶只隽虽双雏杂鸡离难云电沾雾霁雳霭灵靓静靥巩秋鞑千鞯韦韧韩韪韬韫韵响页顶顷项顺顸须顼颂颀颃预顽颁顿颇领颌颉颐颏头颊颔颈颓频颓颗题额颚颜颛颜愿颡颠类颟颢顾颤显颦颅风飑飒台刮飓飕飘飙飞饥饨饪饫饬饭饮饴饲饱饰饺饼饷养饵饽馁饿哺馀肴馄饯馅馆饧饩馏馊馍馒馐馑馈馔饥饶飨餍馋马驭冯驮驰驯驳驻驽驹驵驾骀驸驶驼驷骂骈骇驳骆骏骋骓骒骑骐骛骗骞骘骝腾驺骚骟骡蓦骜骖骠骢驱骁骄验惊驿骤驴骥骊肮髅脏体髋发松胡须鬓斗闹哄阋郁魉魇鱼鲁鲂鱿鲐鲍鲋鲒鲞鲕鲔鲛鲑鲜鲧鲠鲩鲤鲨鲻鲭鲞鲷鲱鲵鲲鲳鲸鲮鲰鲶鲫鲽鳇鳅鳄鳆鳃鲥鳏鳎鳐鳍鲢鳗鳔鳖鳝鳜鳞鲎鳄鲈鸟凫鸠凫凤鸣鸢鸩鸨鸦鸵鸳鸲鸱鸪鸯鸭鸸鸹鸿鸽鸺鹃鹆鹁鹈鹅鹄鹉鹌鹏鹎雕鹊鸫鹑鹕鹗鹜莺鹤鹘鹣鹞鸡鹧鸥鸶鹰鹭鹦鹳鸾卤咸鹾碱盐丽麦麸曲面黄黉点党黩黾鼋鼹齐斋齿龀龅龇龃龆龄出龈龊龉龋龌龙庞龚龟蹿后碱碱谰霉啮颧尸瓮艳痈钟才僳脔谫谳莼蓠岽猬余饷阄沈滟灏骅骣骧纣缵栾棂椤轳轹昵腼腭飙齑戆龛镔镧镬鸬鸷鹂鹇鹚鹨鹩鹪鹫鹬疴疱瘘颞笕笾簖粜糇糍趱酾跞跹蹰躜鼍雠鲚鲟鲡鲣鲦鲶鳌鳓鳕鳝鳟鳢髌黪厘余厮庵暗鳌杯膘别策尝扯吃酬助捶棰唇啖当荡捣抵翻旁痱干杠胳个构拐拐罐钎蚝合核呼胡糊冱碱剿浚愧馈捆捆累狸麻菱溜炉橹罗蒙妙蔑闵奶霓袅袅暖刨碰瓶旗强墙襁勤睿膻虱湿薯搜溯酸坛绦偷颓望嘻鹇泄修锈埙咽胭岩演焰雁燕夭野殷淫愈龠咱皂榨棹跖妆兹鬃钻碱僳伙鳖里么链么钟彝锨抬",
    /**
     * 繁体库
     * */
    FTStr : "丟並亂亙亞伕佇佈佔併來侖侚侶侷俁係俠俵倀倆倉個們倖倣倫偉偪側偵偽傑傖傘備傚傢傭傯傳傴債傷傾僂僅僉僊僑僕僞僥僨僱價儀儂億儈儉儐儔儕儘償優儲儷儺儻儼兇兌兒兗內兩冊冑冪凈凍凜凱別刪剄則剉剋剎剛剝剮剴創剷劃劄劇劉劊劌劍劑勁動務勛勝勞勢勣勱勳勵勸勻匋匭匯匱區協卬卹卻厙厠厭厲厴參叢吳吶呂咷咼員唄唸問啓啞啟啣喚喪喬單喲嗆嗇嗎嗚嗩嗶嘆嘍嘔嘖嘗嘜嘩嘮嘯嘰嘵嘸噁噓噠噥噦噯噲噴噸噹嚀嚇嚌嚕嚙嚦嚨嚮嚳嚴嚶囀囁囂囅囈囉囌囑囪圇國圍園圓圖團坵埡執堅堊堝堯報場塊塋塏塒塗塚塢塤塵塹墊墜墮墳墾壇壓壘壙壚壞壟壢壩壯壺壽夠夢夾奐奧奩奪奬奮妝妳姍姦姪娛婁婦婭媧媯媼媽嫗嫵嫻嬀嬈嬋嬌嬙嬡嬤嬪嬰嬸孃孌孫學孿宮寢實寧審寫寬寵寶將專尋對導尷屆屜屢層屨屬岡峴島峽崍崑崗崙崢崳嵐嶁嶄嶇嶗嶠嶧嶸嶺嶼嶽巋巒巔巰巹帥師帳帶幀幃幗幘幟幣幫幬幵幹幾庫廁廂廄廈廚廟廠廡廢廣廩廬廳弒弔弳張強彈彌彎彙彥彫彿徑從徠復徹忷恆恟恥悅悵悶悽惇惡惱惲惻愛愜愨愴愷愾慄態慍慘慚慟慣慤慪慫慮慳慶慼慾憂憊憐憑憒憚憤憫憮憲憶懇應懌懍懟懣懨懲懶懷懸懺懼懾戀戔戧戩戰戲戶拋挾捨捫捲掃掄掙掛採揀揚換揮揹損搖搗搯搶摀摑摜摟摯摳摶摻撈撐撓撚撟撣撥撫撲撳撻撾撿擁擄擇擊擋擔據擠擬擯擰擱擲擴擷擺擻擼擾攄攆攏攔攖攙攛攜攝攢攣攤攪攬攷敗敘敵數斂斃斕斬斷昇時晉晝暈暉暢暫曄曆曇曉曖曠曬書會朧朮東柵桿梔條梟棄棖棗棟棧棲椏楄楊楓楨業極榐榪榮榿槃構槍槤槧槨槳樁樂樅樑樓標樞樣樸樹樺橈橋機橢橫檁檉檔檜檢檣檯檳檷檸檻櫃櫓櫚櫛櫝櫞櫟櫥櫧櫨櫪櫫櫬櫳櫸櫺櫻欄權欖欽歎歐歟歡歲歷歸歿殘殞殤殫殭殮殯殲殺殻殼殽毀毆毿氈氌氣氫氬氳汆汎汙決沒沖況洶浹涇涼淒淚淥淨淪淵淶淺渙減渦測渾湊湞湧湯溈準溝溫滄滅滌滎滬滯滲滷滸滾滿漁漚漢漣漬漲漵漸漿潁潑潔潙潛潤潯潰潷潿澀澂澆澇澗澠澤澩澮澱濁濃濕濘濛濟濤濫濰濱濺濼濾瀅瀆瀉瀋瀏瀕瀘瀝瀟瀠瀦瀧瀨瀰瀲瀾灃灄灑灕灘灣灤災為烏烴無煉煒煙煢煥煩煬熒熗熱熾燁燈燉燒燙燜營燦燬燭燴燻燼燾爍爐爛爭爲爺爾牆牘牠牽犖犢犧狀狹狽猙猶猻獃獄獅獎獨獪獫獰獲獵獷獸獺獻獼玀玨珮現琺琿瑋瑣瑤瑩瑪瑯璉璣璦環璽瓊瓏瓔瓚甌產産甦畝畢畫畬異當疇疊痙痲痺瘋瘍瘓瘞瘡瘧療癆癇癉癒癘癟癡癢癤癥癩癬癭癮癱癲發皚皸皺盃盜盞盡監盤盧盪眾睏睜睞睪瞇瞞瞭瞼矇矓矚矯砲硃硤硨硯碩碭確碼磚磣磧磯磽礎礙礡礦礪礫礬礱祂祐祕祿禍禎禦禪禮禰禱禿稅稈稟稨種稱穀穌積穎穡穢穩穫窩窪窮窯窶窺竄竅竇竈竊竪競筆筍筧箋箏節範築篋篤篩篳簀簍簞簡簣簫簷簽簾籃籌籜籟籠籤籬籮籲粵糝糞糧糰糲糴糾紀紂約紅紆紇紈紉紋納紐紓純紕紗紙級紛紜紡紮細紱紲紳紹紺紼紿絀終絃組絆絎結絕絛絞絡絢給絨統絲絳絶絹綁綃綆綈綉綏綑經綜綞綠綢綣綫綬維綰綱網綳綴綵綸綹綺綻綽綾綿緄緇緊緋緑緒緗緘緙線緝緞締緡緣緦編緩緬緯緱緲練緶緹緻縈縉縊縋縐縑縛縝縞縟縣縧縫縭縮縱縲縴縵縶縷縹總績繃繅繆繒織繕繚繞繡繢繩繪繫繭繯繰繳繹繼繽繾纈纊續纏纓纖纜缽罌罎罰罵罷羅羆羈羋羥羨義習翹耑耬聖聞聯聰聲聳聵聶職聹聽聾肅胇脅脈脛脫脹腎腡腦腫腳腸膃膆膚膠膩膽膾膿臉臍臏臘臚臟臥臨臺與興舉舊舋舖艙艤艦艫艱艷芻苧茲荊荳莊莖莢莧華萇萊萬萵萹葉葒葦葯葷蒔蒞蒼蓀蓆蓋蓮蓯蓽蔔蔞蔣蔥蔦蔭蕁蕆蕎蕓蕕蕘蕢蕩蕪蕭蕷薈薊薌薑薔薙薟薦薩薺藍藎藝藥藪藴藶藹藺蘄蘆蘇蘊蘋蘚蘞蘢蘭蘿處虛虜號虧虯蛺蛻蜆蝕蝦蝸螄螞螢螻蟄蟈蟣蟬蟯蟲蟶蟻蠅蠆蠍蠐蠑蠟蠣蠱蠶蠻衃衆衒術衛衝衹袞裊補裝裡製複褲褳褸褻襇襉襖襝襠襤襪襯襲見規覓視覘覡覦親覬覯覲覷覺覽覿觀觴觶觸訂訃計訊訌討訐訓訕訖託記訛訝訟訢訣訥訪設許訴訶診註証詁詆詎詐詒詔評詘詛詞詠詡詢詣試詩詫詬詭詮詰話該詳詵詼詿誄誅誆誇誌認誑誒誕誘誚語誠誡誣誤誥誦誨說説誰課誶誹誼調諂諄談諉請諍諏諑諒論諗諛諜諞諠諢諤諦諧諫諭諮諱諳諶諷諸諺諼諾謀謁謂謄謅謊謎謐謔謖謗謙謚講謝謠謡謨謫謬謳謹謾譁證譎譏譔譖識譙譚譜譟譫譭譯議譴護譽讀變讒讓讖讚讜谿豈豎豐豬貓貝貞負財貢貧貨販貪貫責貯貰貲貳貴貶買貸貺費貼貽貿賀賁賂賃賄賅資賈賊賑賒賓賕賙賚賜賞賠賡賢賣賤賦賧質賬賭賴賺賻購賽賾贄贅贈贊贋贍贏贐贓贖贗贛贜趕趙趨跡踐踰踴蹌蹕蹟蹣蹤蹧蹺躉躊躋躍躑躓躡躪軀車軋軌軍軒軔軛軟軫軸軹軺軻軼軾較輅輇載輊輒輓輔輕輛輜輝輞輟輥輦輩輪輯輳輸輻輾輿轂轄轅轆轉轍轎轔轟轡辦辭辮辯農迴迺逕這連週進遊運過達違遙遜遞遠適遲遷選遺遼邁還邇邊邏邐郟郵鄆鄉鄒鄔鄖鄧鄭鄰鄲鄴鄶鄺酈醃醆醖醜醞醫醬釀釁釅釋釓釔釕釗釘釙針釣釤釦釧釩釵釷釹鈀鈁鈄鈉鈍鈎鈐鈑鈔鈕鈞鈣鈥鈦鈧鈮鈰鈳鈴鈷鈸鈹鈺鈽鈾鈿鉀鉅鉈鉉鉋鉍鉑鉗鉚鉛鉞鉢鉤鉦鉬鉭鉸鉺鉻鉿銀銃銅銑銓銖銘銚銜銠銣銥銦銨銩銪銫銬銲銳銷銻銼鋁鋃鋅鋇鋌鋏鋒鋝鋟鋤鋦鋨鋪鋭鋮鋯鋰鋱鋸鋼錁錄錆錈錐錒錕錘錙錚錛錟錠錢錦錨錫錮錯録錳錶錸鍆鍇鍋鍍鍔鍘鍛鍤鍥鍬鍰鍵鍶鍺鍼鍾鎂鎊鎖鎘鎢鎣鎦鎧鎩鎪鎬鎮鎰鎳鎵鎸鏃鏇鏈鏌鏍鏑鏗鏘鏜鏝鏞鏟鏡鏢鏤鏨鏵鏷鏹鏽鐃鐋鐐鐒鐓鐔鐘鐙鐠鐨鐫鐮鐲鐳鐵鐶鐸鐺鐿鑄鑑鑒鑠鑣鑰鑲鑷鑼鑽鑾鑿長門閂閃閆閉開閌閎閏閑閒間閔閘閡閣閥閨閩閫閬閭閱閲閶閹閻閼閽閾閿闃闆闈闊闋闌闐闔闕闖關闞闡闢闥陘陝陞陣陰陳陸陽隄隉隊階隕際隨險隱隴隸隻雋雖雙雛雜雞離難雲電霑霧霽靂靄靈靚靜靨鞏鞦韃韆韉韋韌韓韙韜韞韻響頁頂頃項順頇須頊頌頎頏預頑頒頓頗領頜頡頤頦頭頰頷頸頹頻頽顆題額顎顏顓顔願顙顛類顢顥顧顫顯顰顱風颮颯颱颳颶颼飄飆飛飢飩飪飫飭飯飲飴飼飽飾餃餅餉養餌餑餒餓餔餘餚餛餞餡館餳餼餾餿饃饅饈饉饋饌饑饒饗饜饞馬馭馮馱馳馴駁駐駑駒駔駕駘駙駛駝駟駡駢駭駮駱駿騁騅騍騎騏騖騙騫騭騮騰騶騷騸騾驀驁驂驃驄驅驍驕驗驚驛驟驢驥驪骯髏髒體髖髮鬆鬍鬚鬢鬥鬧鬨鬩鬱魎魘魚魯魴魷鮐鮑鮒鮚鮝鮞鮪鮫鮭鮮鯀鯁鯇鯉鯊鯔鯖鯗鯛鯡鯢鯤鯧鯨鯪鯫鯰鯽鰈鰉鰍鰐鰒鰓鰣鰥鰨鰩鰭鰱鰻鰾鱉鱔鱖鱗鱟鱷鱸鳥鳧鳩鳬鳳鳴鳶鴆鴇鴉鴕鴛鴝鴟鴣鴦鴨鴯鴰鴻鴿鵂鵑鵒鵓鵜鵝鵠鵡鵪鵬鵯鵰鵲鶇鶉鶘鶚鶩鶯鶴鶻鶼鷂鷄鷓鷗鷥鷹鷺鸚鸛鸞鹵鹹鹺鹼鹽麗麥麩麯麵黃黌點黨黷黽黿鼴齊齋齒齔齙齜齟齠齡齣齦齪齬齲齷龍龐龔龜躥後堿鹼讕黴齧顴屍甕豔癰鍾纔傈臠譾讞蓴蘺崠蝟餘饟鬮瀋灩灝驊驏驤紂纘欒欞欏轤轢暱靦齶飆齏戇龕鑌鑭鑊鸕鷙鸝鷴鶿鷚鷯鷦鷲鷸痾皰瘺顳筧籩籪糶餱餈趲釃躒躚躕躦鼉讎鱭鱘鱺鰹鰷鯰鰲鰳鱈鱔鱒鱧髕黲釐餘廝菴闇鼇桮臕彆筴嚐撦喫詶耡搥箠脣啗儅盪擣觝繙徬疿榦槓肐箇搆枴柺鑵釬蠔閤覈謼衚餬沍硷勦濬媿餽梱綑纍貍痳蔆霤鑪艣儸懞玅衊湣嬭蜺嫋嬝煖鑤踫缾旂彊墻繈懃叡羶蝨溼藷蒐泝痠罈縚媮穨朢譆鷳洩脩銹壎嚥臙巖縯燄鴈鷰殀埜慇婬瘉籥偺皁搾櫂蹠粧玆騣鉆硷傈夥鼈裏麽鍊麼锺彜鍁擡",
    /**
     * 繁体转化简体
     * */
    Simplized : function (cc){
 	    var str='',jt=this.JTStr,ft=this.FTStr;
 	    for(var i=0;i<cc.length;i++){
 	       if(cc.charCodeAt(i)>10000&&ft.indexOf(cc.charAt(i))!=-1)str+=jt.charAt(ft.indexOf(cc.charAt(i)));
 	          else str+=cc.charAt(i);
 	    }
 	    return str;   
    },
    Traditionalized :function (cc){
 	    var str='',ss=this.JTStr,tt=this.FTStr;
 	    for(var i=0;i<cc.length;i++){
 	        if(cc.charCodeAt(i)>10000&&ss.indexOf(cc.charAt(i))!=-1)str+=tt.charAt(ss.indexOf(cc.charAt(i)));
 	          else str+=cc.charAt(i);
 	    }
 	    return str;
 	},
 	numberFormat:function(a, b) {
 		var me = this;
    	if(a < 0){
    		return -1*Math.round(me.multiply(Math.abs(a), Math.pow(10, b)))/Math.pow(10, b);
    	} else {
    		return Math.round(me.multiply(a, Math.pow(10,b)))/Math.pow(10, b);
    	}
	},
	/**
	 * 两数相乘，解决js乘法浮点错误
	 */
	multiply: function(a, b) {
		var m = 0, _a = String(a), _b = String(b);
		try {
			m += _a.split(".")[1].length;
		} catch(e) {
		}
		try {
			m += _b.split(".")[1].length;
		} catch(e) {
		}
		return Number(_a.replace(".", ""))*Number(_b.replace(".", ""))/Math.pow(10, m);
	},
	addListener: function(eventName, fn, scope, options) {// extjs event buffer属性有bug
		if(scope && typeof scope.addListener === 'function') {
			var opts = options || {}, buffer = opts.buffer || 1000;
			delete opts.buffer;
			scope.on(eventName, function(b, e){
				if(!scope.__buffered) {
					scope.__buffered = true;
					fn && fn.call(scope);
					Ext.defer(function(){
						delete scope.__buffered;
					}, buffer);
				} else {
					e.preventDefault();
					e.stopEvent();
				}
			}, scope, opts);
		}
	},
	/**
	 * 通达新成套发料界面4.2版本导出处理
	 */
	createExcel_version: function(caller, type, condition, title, remark, customFields, grid){
		var frm = document.createElement('form'); 
		condition = condition == null ? '' : condition;
		title = this.pageTitle(title);
		var a = Ext.fly('ext-grid-excel');
		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = 'ext-window';  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		} 
		var bool = true, _noc = this.getUrlParam("_noc"), lg = 0;// lg = 1表示大数据
		_noc = _noc || (type == 'detailgrid' ? 1 : (grid ? grid._noc : 0));
		Ext.Ajax.request({
			url: basePath + 'common/beforeExport.action',
			params: {
				caller: caller,
				type: type,
				_self:getUrlParam('_self'),
				condition: condition
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
					bool = false;
				} else if(rs.busy) {
					showMessage('下载提示', '<h1>需要导出数据的人过多</h1>请稍后再试...');
					bool = false;
				} else {
					 if(rs.count > 100000) {
						 showMessage('下载提示', '<h1>数据量过大</h1>当前总数据为' + rs.count + '条，超过导出上限(10万条)，系统将为您导出前10万条<br>请稍等...');
					 }
					 lg = rs.count > 5000 ? 1 : 0;
				}
			}
		});
		if(!bool) return;
		if(remark){
			Ext.Ajax.request({
				url: basePath + 'common/excel/gridWithRemark.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					caller: caller,
					type: type,
					title: unescape(title.replace(/\\/g,"%").replace(/,/g," ")),
					condition: condition,
					remark: unescape(remark.replace(/\\/g,"%")),
					fields : customFields,
					_noc: _noc,
					lg: lg
				}
			});
		} else {
			Ext.Ajax.request({
				url: basePath + 'common/excel/create.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					caller: caller,
					type: type,
					title: unescape(title),
					condition: condition,
					fields : customFields,
					_noc: _noc,
					_self:getUrlParam('_self'),
					lg: lg
				}
			});
		}
	}
});