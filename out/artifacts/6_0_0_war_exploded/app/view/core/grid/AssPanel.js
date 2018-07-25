/**
 * 辅助核算gridPanel
 */
Ext.define('erp.view.core.grid.AssPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.assgrid',
	layout : 'fit',
	id: 'assgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    features: [Ext.create('Ext.grid.feature.Grouping',{
    	startCollapsed: true,
        groupHeaderTpl: 'VoucherDetail:{name} (count:{rows.length})'
    })],
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    detno: '',
    keyField: '',
    necessaryField: '',
    mainField: '',
    condition:'',
    listeners : {
    	itemclick : {
    		fn : function(){
    			this.onGridItemClick(arguments[0],arguments[1]);
    		}
    	}
    },
    initComponent : function(){ 
		this.callParent();
		var formCondition1 = this.BaseUtil.getUrlParam('formCondition');
		gridCondition = this.BaseUtil.getUrlParam('gridCondition'); 
    	gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
    	var formconfig = (formCondition1 == null) ? ['',''] : formCondition1.replace(/IS/g,"=").split('=');
    	var gridParam = {caller: this.condition, condition: " mta_caller ='"+caller+"' and mta_keyid='"+formconfig[1]+"'"};
    	this.getMyData(this, 'common/singleGridPanel.action?', gridParam, "");
    	
	},
	beforeDelete : function(btn,grid){
		var records = grid.selModel.getSelection();
		if(records.length > 0){
			if(grid.keyField){
				if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
					warnMsg($I18N.common.msg.ask_del, function(btn){
						if(btn == 'yes'){
							grid.BaseUtil.getActiveTab().setLoading(true);//loading...
							Ext.Ajax.request({
						   		url : basePath + "common/deleteDetail.action",
						   		params: {
						   			caller: caller+'_main',
						   			condition: grid.keyField + "=" + records[0].data[grid.keyField]
						   		},
						   		method : 'post',
						   		callback : function(options,success,response){
						   			grid.BaseUtil.getActiveTab().setLoading(false);
						   			var localJson = new Ext.decode(response.responseText);
						   			if(localJson.exceptionInfo){
					        			showError(localJson.exceptionInfo);return;
					        		}
					    			if(localJson.success){
					    				grid.store.remove(records[0]);
						   				delSuccess(function(){
								   										
										});//@i18n/i18n.js
						   			} else {
						   				delFailure();
						   			}
						   		}
							});
						}
					});
				} else {
					grid.store.remove(records[0]);
				}
			} else {
				if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
					showError("grid未配置keyField，无法删除该行数据!");
				} else {
					grid.store.remove(records[0]);
				}
			}
		}
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('windowgrid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.onGridItemClick1(selModel, record);
    },
    onGridItemClick1: function(selModel, record, id){
		var me = this;
		var grid = id == null ? Ext.getCmp('windowgrid') : (Ext.getCmp(id) || Ext.getCmp('windowgrid'));
		if(grid && !grid.readOnly){
			var index = null;
			if(grid.detno){
				index = record.data[grid.detno];
				index = index == null ? (record.index + 1) : index;
				if(index.toString() == 'NaN'){
					index = '';
				}
				if(index == grid.store.data.items[grid.store.data.items.length-1].data[grid.detno]){//如果选择了最后一行
					me.add10EmptyItems(grid);//就再加10行
		    	}
			} else {
				index = record.index + 1;
				if(index.toString() == 'NaN'){
					index = '';
				}
				if(index == grid.store.data.items[grid.store.data.items.length-1].index + 1){//如果选择了最后一行
					me.add10EmptyItems(grid);//就再加10行
		    	}
			}

		}
	},
	
	beforeUpdate: function(){

		var mm = this;
		var s2 = '';
		var datalist = this.store.data.items;
		var updateDetno = new Array();
		var index=0;
		var jsonGridData = new Array();
//		var haveDate = false;
		if(datalist[0].data[this.necessaryField].length<=0||null==datalist[0].data[this.necessaryField]||''==datalist[0].data[this.necessaryField]){
			warnMsg('辅助核算表中还没有添加数据');
		}else{
				Ext.Array.each(datalist,function(item){
					if(item.dirty){
						updateDetno[index++]=item.index;
//						updateDetno.push(item.index);
					}
				});
				var alertMsg = '';
				if(updateDetno.length <= 0 ){
					alertMsg='辅助核算表中没有修改数据';
				}else{
					alertMsg='已经修改第';
					for(var i=0;i<updateDetno.length;i++){
						alertMsg=alertMsg+(updateDetno[i]+1);
						if(i!=(updateDetno.length-1)){
							alertMsg=alertMsg+',';
						}
					}
					alertMsg=alertMsg+'行数据';
				}
				
				warnMsg(alertMsg,function(btn){
					if(btn =='yes'){
						for(var i=0;i<datalist.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
							var data = datalist[i].data;
							if(datalist[i].dirty && data[mm.necessaryField] != null && data[mm.necessaryField] != ""){
								jsonGridData.push(Ext.JSON.encode(data));
							}
						}
						var params = [];
						params = unescape("[" + jsonGridData.toString().replace(/\\/g,"%") + "]");
						mm.update(params);
					}
					
				});
				
	    	}

		},
	update:function(param){

		Ext.Ajax.request({
	   		url : basePath+'fa/ars/saveAss.action',
	   		params: {
	   			param: param,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				updateSuccess(function(btn){
//    					//update成功后刷新页面进入可编辑的页面 

    				    var main = Ext.getCmp('erpasswin_'+caller);
    					    main.close();
	   				});
	   			} else if(localJson.exceptionInfo){
	   				
	   			} else {
	   				updateFailure();
	   			}
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
	getMyData: function(grid, url, param, no){

		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: true,
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			Ext.each(res.columns, function(column, y){
        				//render
        				if(!column.haveRendered && column.renderer != null && column.renderer != ""){
        					if(!grid.RenderUtil){
        						grid.RenderUtil = Ext.create('erp.util.RenderUtil');
        					}
                    		var renderName = column.renderer;
                    		if(contains(column.renderer, ':', true)){
                    			var args = new Array();
                    			Ext.each(column.renderer.split(':'), function(a, index){
                    				if(index == 0){
                    					renderName = a;
                    				} else {
                    					args.push(a);
                    				}
                    			});
                    			if(!grid.RenderUtil.args[renderName]){
                    				grid.RenderUtil.args[renderName] = new Object();
                    			}
                    			grid.RenderUtil.args[renderName][column.dataIndex] = args;
                    		}
                    		column.renderer = grid.RenderUtil[renderName];
                    		column.haveRendered = true;
                    	}
        				//logictype
        				var logic = column.logic;
        				if(logic != null){
        					if(logic == 'detno'){
            					grid.detno = column.dataIndex;
            				} else if(logic == 'keyField'){
            					grid.keyField = column.dataIndex;
            				} else if(logic == 'mainField'){
            					grid.mainField = column.dataIndex;
            				} else if(logic == 'necessaryField'){
            					grid.necessaryField = column.dataIndex;
            					if(!grid.necessaryFields){
            						grid.necessaryFields = new Array();
            					}
            					grid.necessaryFields.push(column.dataIndex);
            					if(!column.haveRendered){
            						column.renderer = function(val){
            							   return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
            					  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
            					   };
            					}
            				} else if(logic == 'groupField'){
            					grid.groupField = column.dataIndex;
            				}
        				}
        			});
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			me.add10EmptyData(grid.detno, data);
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			//me.add10EmptyData(grid.detno, data);
            		}
            		var store = Ext.create('Ext.data.Store', {
            		    storeId: 'gridStore',
            		    fields: res.fields,
            		    data: data,
            		    groupField: grid.groupField,
            		    getSum: function(records, field) {
            	            var total = 0,
            	                i = 0,
            	                len = records.length;
            	            for (; i < len; ++i) {
            	            	//重写getSum,grid在合计时，只合计填写了必要信息的行
            	            	var necessary = records[i].get(grid.necessaryField);
            	            	if(necessary != null && necessary != ''){
            	            		total += records[i].get(field);
            	            	}
            	            }
            	            return total;
            		    },
            		    getCount: function() {
            		    	var count = 0;
            		    	Ext.each(this.data.items, function(item){//重写getCount,grid在合计时，只合计填写了必要信息的行
            		    		if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
            		    			count++;
            		    		}
            		    	});
            		        return count;//this.data.length || 0;
            		    }
            		});
            		if(grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		if(res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
    				//toolbar
            		var items = [];
            		var bool = true;
            		Ext.each(grid.dockedItems.items, function(item){
        				if(item.dock == 'bottom' && item.items){//bbar已存在
        					bool = false;
        				}
        			});
            		if(bool){
                		Ext.each(res.columns, function(column){
                			if(column.summaryType == 'sum'){
                				items.push('-',{
                					id: column.dataIndex + '_sum',
                					itemId: column.dataIndex,
                					xtype: 'tbtext',
                					text: column.header + '(sum):0'
                				});
                			} else if(column.summaryType == 'average') {
                				items.push('-',{
                					id: column.dataIndex + '_average',
                					itemId: column.dataIndex,
                					xtype: 'tbtext',
                					text: column.header + '(average):0'
                				});
                			} else if(column.summaryType == 'count') {
                				items.push('-',{
                					id: column.dataIndex + '_count',
                					itemId: column.dataIndex,
                					xtype: 'tbtext',
                					text: column.header + '(count):0'
                				});
                			}
                			if(column.dataIndex == res.necessaryField){
                				column.renderer = function(val){
                					return '<img src="' + basePath + 'resource/images/renderer/necessary.png" title="必填字段">' + 
                			   		'<span style="color:blue;padding-left:2px" title="必填字段">' + val + '</span>';
                				};
                			}
                		});
            			grid.addDocked({
                			xtype: 'toolbar',
                	        dock: 'bottom',
                	        items: items
                		});
            		} else {
            			var bars = Ext.ComponentQuery.query('erpToolbar');
            			if(bars.length > 0){
            				Ext.each(res.columns, function(column){
                    			if(column.summaryType == 'sum'){
                    				bars[0].add('-');
                    				bars[0].add({
                    					id: column.dataIndex + '_sum',
                    					itemId: column.dataIndex,
                    					xtype: 'tbtext',
                    					text: column.header + '(sum):0'
                    				});
                    			} else if(column.summaryType == 'average') {
                    				bars[0].add('-');
                    				bars[0].add({
                    					id: column.dataIndex + '_average',
                    					itemId: column.dataIndex,
                    					xtype: 'tbtext',
                    					text: column.header + '(average):0'
                    				});
                    			} else if(column.summaryType == 'count') {
                    				bars[0].add('-');
                    				bars[0].add({
                    					id: column.dataIndex + '_count',
                    					itemId: column.dataIndex,
                    					xtype: 'tbtext',
                    					text: column.header + '(count):0'
                    				});
                    			}
                    		});
            			}
            		}
            		grid.reconfigure(store, res.columns);
            		var form = Ext.ComponentQuery.query('form')[0];
        			if(form && form.readOnly){
        				grid.readOnly = true;//grid不可编辑
        			}
        		} else {
        			grid.hide();
        			var height = window.innerHeight;
        			var form = Ext.ComponentQuery.query('form')[0];
        			if(form){
        				form.setHeight(height);
        			}
        		}
        	}
        });
	},
	add10EmptyData: function(detno, data){
		var formCondition1 = this.BaseUtil.getUrlParam('formCondition');
		var formconfig = (formCondition1 == null) ? ['',''] : formCondition1.replace(/IS/g,"=").split('=');
		var mta_keyid = formconfig[1]=='' ? 0 :formconfig[1];
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1].raw[detno]);
			for(var i=0;i<7;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				o['mta_caller']=caller;
				o['mta_keyid']=mta_keyid;
				data.push(o);
			}
		} else {
			for(var i=0;i<7;i++){
				var o = new Object();
				o['mta_caller']=caller;
				o['mta_keyid']=mta_keyid;
				data.push(o);
			}
		}
	},
	add10EmptyItems: function(grid){
		var items = grid.store.data.items;
		var detno = grid.detno;
		var formCondition1 = this.BaseUtil.getUrlParam('formCondition');
		var formconfig = (formCondition1 == null) ? ['',''] : formCondition1.replace(/IS/g,"=").split('=');
		var mta_keyid = formconfig[1]=='' ? 0 :formconfig[1];
		if(detno){
			var index = items.length == 0 ? 0 : Number(items[items.length-1].data[detno]);
			for(var i=0;i<10;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				o['mta_caller']=caller;
				o['mta_keyid']=mta_keyid;
				grid.store.insert(items.length, o);
				items[items.length-1]['index'] = items.length-1;
			}
		} else {
			for(var i=0;i<10;i++){
				var o = new Object();
				grid.store.insert(items.length, o);
				o['mta_caller']=caller;
				o['mta_keyid']=mta_keyid;
				items[items.length-1]['index'] = items.length-1;
			}
		}
		
	}
});