Ext.QuickTips.init();
Ext.define('erp.controller.ma.update.DataUpdate', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.GridUtil', 'erp.util.BaseUtil'],
	views : [ 'ma.update.DataUpdate', 'core.toolbar.Toolbar' ],
	init : function() {
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
		me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'#updategrid':{
				afterrender : function(f) {
					f.addGrid= function(){
				    me.createGrid(this, Ext.getCmp('scheme_id_').value,Ext.getCmp('scheme_id_').rawValue, true);
					};
				}
			},
			'button[name=import]' : {
				click : function(btn) {
					me.onQueryClick();
				},
				afterrender : function() {
					Ext.defer(function(){
						me.onQueryClick();
					}, 500);
				}
			},
			'button[id=checkupdate]':{			
					click:function(btn){
						if(btn.activeItem.itemIndex==1)me.checkData();
						else me.updateData();					        
					}				
			},
			'button[name=history]' : {
				click : function(btn) {
					me.history();				
				}
			},
			'button[name=close]' : {
				click : function() {
					if (parent.Ext && parent.Ext.getCmp('content-panel')) {
						parent.Ext.getCmp('content-panel').getActiveTab().close();
					} else {
						window.close();
					}
				}
			}, 
			'button[name=downloadError]' : {
			
				click : function() {
					grid=Ext.getCmp('updategrid-data');	
					window.location = basePath + 'ma/update/exportErrorExcel.xls?id=' +grid.ulid+'&title='+encodeURI(encodeURI("错误数据")) ;
				}
			}
			
			
			
		});

	},
	onQueryClick : function() {
		var me = this, win = me.querywin;
		if (!win) {
			var form  = me.createQueryForm(), temp = me.createTempForm();
			win = me.querywin = Ext.create('Ext.window.Window', {
				closeAction : 'hide',
				closable:false,
				title : '选择更新方案',
				height: 500,
        		width: 800,
        		id:'scheme-win',
        		layout: 'anchor',
        		modal: true,
				items : [form, temp],
				buttonAlign : 'center',
				buttons : [{
					text : '查看历史',
					iconCls: 'history',
					cls : 'x-btn-gray',
					height : 26,
					width : 100,
					handler : function(b) {
						me.history();						
					}
				},{
					text : '下载模板',
					iconCls: 'x-button-icon-excel',
					cls : 'x-btn-gray',
					height : 26,
					width : 100,
					handler : function(b) {
						me.exportExcel();						
					}
				},{
					xtype: 'form',
					height : 26,
					width : 100,
					bodyStyle : 'background: transparent no-repeat 0 0;border: none;',
					items : [ {	xtype : 'filefield',
						name : 'file',
						buttonOnly : true,
						hideLabel : true,
						buttonConfig : {
							iconCls : 'x-button-icon-excel',
							cls : 'x-btn-gray',
							width : 100,
							height : 26,
							text : $I18N.common.button.erpUpExcelButton
						},
						listeners : {
							change : function(field) {
								
								me.importData(field);
							}
						}} ],
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					width : 100,
					cls : 'x-btn-gray',
					handler : function(b) {
						if(!Ext.getCmp('updategrid-data')){
							Ext.getCmp('history').setDisabled(true);
							Ext.getCmp('checkupdate').setDisabled(true);
						}
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	createQueryForm:function(){
    	var me = this;
    	var form = Ext.create('Ext.form.Panel', {
    		region: 'center',
    		anchor: '100% 10%',   		
    		layout: 'column',
    		autoScroll: true,
    		defaults: {
    			columnWidth: 1,
    			margin: '4 8 4 8'
    		},
    		bodyStyle: 'background:#f1f2f5;',   
    		items:[{
				xtype:'combo',
				flex:1,
				fieldLabel:'更新方案',
				allowBlank:false,
				name:'scheme_id_',
				id:'scheme_id_',
				listConfig:{
					maxHeight:180
				},
				store: schemeStore,
				displayField: 'title_',
				valueField: 'id_',
				queryMode: 'local',
				editable:false,
				triggerAction : 'all',				
			      listeners: {
			          afterRender : function(combo) {
			        	  if( schemeStore.getAt(0)!=null){
			              var firstValue = schemeStore.getAt(0).get('id_');
			              combo.setValue(firstValue);
			              me.getUpdateDetail(firstValue);}		
			           },
			        	select: function (combo, record, index) {		        
			        		me.getUpdateDetail(record[0].data.id_);				        					        		
			        		
			        	}
			        }
			}]
    	});
    	return form;
	},
    createTempForm : function() {
    	var me = this;
    	return Ext.create('Ext.form.Panel', {
    		id: 'temp',    		
			title: '可修改信息',
			anchor: '100% 90%',
			bodyStyle: 'background:#f1f1f1;',
			autoScroll : true,
			items:[
			       { id: 'tempContainer',
			    	 xtype: 'fieldcontainer',
                     defaultType: 'checkboxfield',
                     items: [],
                     layout:'column',       			
         			 fieldDefaults: {
						margin: '3 10 3 10'
					}
			       }]
    		});	
		},
	getUpdateDetail:function(id){
		var me = this;
		Ext.Ajax.request({
        	url : basePath + 'ma/update/getUpdateDetail.action',
        	params: {
        		id: id,
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		if(!res.success){
        			return;
        		} else {
        			me.indexfields = res.indexfields;
        			me.updatedetails = res.updatedetails;  
        			schemeDetails=me.indexfields.concat(me.updatedetails);
        	    	var b=me.indexfields,d = me.updatedetails, mainitems = new Array();
            		if(b && b.length > 0) {
            			Ext.each(b, function(i){
            				mainitems.push({
        						xtype: 'checkbox',
        						name: i.field_,
        						boxLabel:i.caption_!='null'?'<font color=red>'+i.caption_+'</font>':'<font color=red>'+i.field_+'</font>',
        						checked:true,
        						columnWidth: 0.25,
        						readOnly:true
        					});					
            			});
            		}	   	
        			if(d && d.length > 0) {
        				Ext.each(d, function(i){
        				mainitems.push({
        							xtype: 'checkbox',
        							name: i.field_,
        							boxLabel: i.caption_,
        							columnWidth: 0.25,
        							checked:i.checked_
        						});					
        				});
        			}
        			Ext.getCmp('tempContainer').removeAll();
        			Ext.getCmp('tempContainer').add(mainitems);
	        		Ext.getCmp('temp').doLayout(true);
        		}
        	}
        });
	},
	exportExcel:function(){
		var me=this;
		var items = Ext.getCmp('tempContainer').items.items;
		var checked = new Array();
		Ext.each(items,function(item){
			if(!item.readOnly && item.checked)checked.push(item.name);
		});
		if (checked.length!=0){
			window.location = basePath + 'ma/update/exportExcel.xls?id=' + Ext.getCmp('scheme_id_').value + 
			'&title=' + (Ext.getCmp('scheme_id_').rawValue)+'&checked='+unescape(checked);
		}else showError("请选择待更新的字段");		
	},
	importData:function(field){
		var me=this;
		var bool = field.ownerCt.fireEvent('beforeimport', this);
		if (bool != false) {
			field.ownerCt.getForm().submit({
        		url: basePath + 'ma/update/importData.action?id=' + Ext.getCmp('scheme_id_').value,
        		waitMsg: "正在解析Excel",
        		success: function(fp, o){
        			field.reset();   
        			me.createGrid(Ext.getCmp('updategrid'), Ext.getCmp('scheme_id_').value,Ext.getCmp('scheme_id_').rawValue, true);
        			Ext.getCmp('updategrid-data').ulid = o.result.ulid;
        			Ext.getCmp('updategrid-data').down('pagingtoolbar').dataCount = o.result.count;
        			Ext.getCmp('updategrid-data').down('pagingtoolbar').onLoad();
        			Ext.getCmp('updategrid-data').getGridData(1);
        			Ext.getCmp('checkupdate').setDisabled(false);
        			Ext.getCmp('history').setDisabled(false);
        			Ext.getCmp('downloaderror').hide();
        			
        			field.ownerCt.ownerCt.ownerCt.hide();
        		},
        		failure: function(fp, o){
        			if(o.result.size){
        				showError(o.result.error + "&nbsp;" + Ext.util.Format.fileSize(o.result.size));
        				field.reset();
        			} else {
        				showError(o.result.error);
        				field.reset();
        			}
        		}
        	});
		}
	},
	emptyData: function(arr){
    	var fields = new Array();
    	Ext.each(arr, function(a){
    		fields.push(a.field_);
    	});
    	return new Ext.data.Store({
    		fields: fields,
    		data: [{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}]
    	});
    },
    parseUpdateDetails: function(arr){
    	var me = this, d = new Array(),o;
    	d.push({xtype: 'rownumberer', width: 35});
    	Ext.each(arr, function(a){
    		o = new Object();
    		o.text = a.caption_;
    		o.dataIndex = a.field_;
    		o.width = a.width_;    		
    		o.dataType = a.type_;
    		o.editor = {
    				xtype: 'textfield'
    		};
    		o.renderer = function(val, meta, record, x, y, store, view){
    			return val;
    		};
    		d.push(o);
    	});
    	return d;
    },
    loadUpdateData: function(grid, page){
    	var f = (page-1) * 100 + 1,
    		t = page*100;
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'ma/update/getUpdateData.action',
    		params: {
    			condition: "ud_ulid=" + grid.ulid + 
    				" AND ud_detno between " + f + " AND " + t
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
    			grid.setLoading(false);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else {
        			var datas = new Array(), o;
        			Ext.each(res.data, function(d){
        				o = Ext.decode(d.ud_data);
        				o.ud_id = d.ud_id;
        				datas.push(o);
        			});
        			grid.store.loadData(datas);        		
        		}
    		}
    	});
    },
	createGrid: function(p, id,title, isReload) {
		var me=this;
		var store = me.emptyData(schemeDetails);
		var updategrid= Ext.create('Ext.grid.Panel', {
			anchor: '100% 100%',
			id : 'updategrid-data',		
			cls : 'default-grid',
			cfg: schemeDetails,//当前导入项的配置信息
			ulid:-1,
			checked:0,
			columnLines: true,
			columns: me.parseUpdateDetails(schemeDetails),
			store: store,
			dockedItems: [me.getDockedItems(store)],
			getGridData: function(page){
				me.loadUpdateData(this, page);
			    },
			loadData: function(ulid, count, page){
			    	this.ulid = ulid;
			    	this.down('pagingtoolbar').dataCount = count;
    			    this.down('pagingtoolbar').onLoad();
			    	this.getGridData(page);			    
			    }	
		});
		p.add(updategrid);
	},
	loadDefaultData: function(grid, page,ulid){
    	var f = (page-1) * 100 + 1,
    		t = page*100;
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'ma/update/getUpdateData.action',
    		params: {
    			condition: "ud_ulid =" + grid.ulid + 
    				"AND UD_CHECKED=0 AND ud_detno between " + f + " AND " + t
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
    			grid.setLoading(false);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else {
        			var datas = new Array(), o;
        			Ext.each(res.data, function(d){
        				o = Ext.decode(d.ud_data);
        				o.ud_id = d.ud_id;
        				datas.push(o);
        			});
        			grid.store.loadData(datas);        		
        		}
    		}
    	});
    },
	
	
	
    checkData:function(){
		var me=this,grid=Ext.getCmp('updategrid-data');		
		grid.setLoading(true);
		Ext.Ajax.request({
    		url: basePath + 'ma/update/checkData.action',
    		params: {
    			ulid:grid.ulid
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
    			grid.setLoading(false);
        		if(res.exceptionInfo != null){   
        			/*console.log(Ext.getCmp('checkupdate').activeItem.itemIndex);
        			Ext.getCmp('checkupdate').activeItem.itemIndex=1;*/
					Ext.getCmp('downloaderror').show();
        			me.loadDefaultData(grid,1);
        			
        			showError(res.exceptionInfo);return;
        		} else {
        			grid.checked=1;
        			Ext.getCmp('downloaderror').hide();
        			alert('校验成功!');
        		}
    		}
    	});	
    },
	updateData: function(){
		var me=this,grid=Ext.getCmp('updategrid-data');
		if(grid.checked==1){
		grid.setLoading(true);
		Ext.Ajax.request({
    		url: basePath + 'ma/update/updateData.action',
    		params: {
    			ulid:grid.ulid
    		},
    		method: 'post',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
    			grid.setLoading(false);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else {
        			alert('更新成功!');
        		}
    		}
    	});}
		else showError('请先校验数据!');
	},
	history:function(){
    	var w = Ext.create('Ext.Window', {
    		width: '60%',
    		height: '80%',
    		id: 'history-win',
    		title: '更新数据历史记录',
    		autoShow: true,
    		layout: 'anchor',
    		items: [{
    			xtype: 'gridpanel',
    			anchor: '100% 100%',
    			columnLines: true,
    			columns: [{dataIndex: 'ul_id', hidden: true},{dataIndex: 'ul_usid', hidden: true},{dataIndex: 'ul_man', text: '更新人编号', flex: 1},
    					  {dataIndex: 'ul_date', text: '日期', flex: 2},
    			          {dataIndex: 'ul_count', text: '数据量(条)', flex: 1},
    			          {dataIndex: 'ul_checked', text: '是否校验通过', flex: 1, renderer: function(val, m){
    			        	  if(val == '否') {
    			        		  m.style = 'float:right';
    			        	  }
    			        	  return val;
    			          }}, {dataIndex: 'ul_success', text: '是否更新成功', flex: 1, renderer: function(val, m){
    			        	  if(val == '否') {
    			        		  m.style = 'float:right';
    			        	  }
    			        	  return val;
    			          }}, {text: '', flex: 1, renderer: function(val, m, r){
    			        	  return '<a href="javascript:Ext.getCmp(\'updategrid\').addGrid();Ext.getCmp(\'updategrid-data\').loadData(' + r.get('ul_id') + ',' 
    			        	  		+ r.get('ul_count') + ',1);Ext.getCmp(\'history-win\').close();Ext.getCmp(\'scheme-win\').close();Ext.getCmp(\'checkupdate\').setDisabled(true);Ext.getCmp(\'history\').setDisabled(false);">载入</a>';	        	      			        	  
    			          }}],
    			store: Ext.create('Ext.data.Store', {
    				fields: ['ul_id', 'ul_usid', 'ul_man', 'ul_date', 'ul_count', 'ul_checked', 'ul_success'],
    				data: [{},{},{},{},{},{},{},{}]
    			})
    		}]
    	});
    	this.getUpdateLog(w.down('gridpanel'));
    },
    getUpdateLog: function(g){  	
    	Ext.Ajax.request({
    		url: basePath + 'ma/update/updateHistory.action',
    		params: {
    			id: Ext.getCmp('scheme_id_').value
    		},
    		method: 'post',
    		callback: function(opt, s, r){
    			var res = new Ext.decode(r.responseText);
    			var dd = res.data;
    			Ext.each(dd, function(d){
    				d.ul_date = Ext.Date.format(new Date(d.ul_date), 'Y-m-d H:i:s');
    				d.ul_checked = d.ul_checked == 1 ? '是' : '否';
    				d.ul_success = d.ul_success == 1 ? '是' : '否';
    			});
    			g.store.loadData(dd);
    		}
    	});
    },
    getDockedItems: function(store){
    	return {
    		xtype: 'pagingtoolbar',
    		store: store,
    		pageSize: 100,
    		dataCount: store.data.items.length,
    		page: 1,
    		dock: 'bottom',
    		displayInfo: true,
    		updateInfo : function(){
    			var page = this.child('#inputItem').getValue() || 1;
    			var me = this,
    				pageSize = me.pageSize || 100,
    				dataCount = me.dataCount || 20;
	 	    	var displayItem = me.child('#displayItem'),
	 	    	 	pageData = me.getPageData();
                pageData.fromRecord = (page-1)*pageSize+1;
                pageData.toRecord = page*pageSize > dataCount ? dataCount : page*pageSize;
	    		pageData.total = dataCount;
	    		var msg;
	                if (displayItem) {
	                    if (me.dataCount === 0) {
	                        msg = me.emptyMsg;
	                    } else {
	                        msg = Ext.String.format(
	                            me.displayMsg,
	                            pageData.fromRecord,
	                            pageData.toRecord,
	                            pageData.total
	                        );
	                    }
	                    displayItem.setText(msg);
	                    me.doComponentLayout();
	                }
	            },
	            getPageData : function(){
	            	var me = this,
	            		totalCount = me.dataCount;
		        	return {
		        		total : totalCount,
		        		currentPage : me.page,
		        		pageCount: Math.ceil(me.dataCount / me.pageSize),
		        		fromRecord: ((me.page - 1) * me.pageSize) + 1,
		        		toRecord: Math.min(me.page * me.pageSize, totalCount)
		        	};
		        },
		        doRefresh:function(){
			    	this.moveFirst();
			    },
		        onPagingKeyDown : function(field, e){
		            var me = this,
		                k = e.getKey(),
		                pageData = me.getPageData(),
		                increment = e.shiftKey ? 10 : 1,
		                pageNum = 0;

		            if (k == e.RETURN) {
		                e.stopEvent();
		                pageNum = me.readPageFromInput(pageData);
		                if (pageNum !== false) {
		                    pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
		                    me.child('#inputItem').setValue(pageNum);
		                    if(me.fireEvent('beforechange', me, pageNum) !== false){
		                    	me.page = pageNum;
		                    	me.ownerCt.getGridData(me.page);
		                    }
		                    
		                }
		            } else if (k == e.HOME || k == e.END) {
		                e.stopEvent();
		                pageNum = k == e.HOME ? 1 : pageData.pageCount;
		                field.setValue(pageNum);
		            } else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN) {
		                e.stopEvent();
		                pageNum = me.readPageFromInput(pageData);
		                if (pageNum) {
		                    if (k == e.DOWN || k == e.PAGEDOWN) {
		                        increment *= -1;
		                    }
		                    pageNum += increment;
		                    if (pageNum >= 1 && pageNum <= pageData.pages) {
		                        field.setValue(pageNum);
		                    }
		                }
		            }
		            me.updateInfo();
		            me.resetTool(value);
		        }, 
		        moveFirst : function(){
	            	var me = this;
	                me.child('#inputItem').setValue(1);
	                value = 1;
	            	me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            movePrevious : function(){
	                var me = this;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue() - 1);
	                value = me.child('#inputItem').getValue();
	                me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            moveNext : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total = last;
	                me.child('#inputItem').setValue(me.child('#inputItem').getValue() + 1);
	                value = me.child('#inputItem').getValue();
	                me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            moveLast : function(){
	                var me = this,
	                last = me.getPageData().pageCount;
	                total = last;
	                me.child('#inputItem').setValue(last);
	                value = me.child('#inputItem').getValue();
	            	me.page = value;
	            	me.ownerCt.getGridData(value);
	                me.updateInfo();
	                me.resetTool(value);
	            },
	            onLoad : function() {
					var e = this, d, b, c, a;
					if (!e.rendered) {
						return ;
					}
					d = e.getPageData();
					b = d.currentPage;
					c = Math.ceil(e.dataCount / e.pageSize);
					a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
					e.child("#afterTextItem").setText(a);
					e.child("#inputItem").setValue(b);
					e.child("#first").setDisabled(b === 1);
					e.child("#prev").setDisabled(b === 1);
					e.child("#next").setDisabled(b === c || c===1);//
					e.child("#last").setDisabled(b === c || c===1);
					e.child("#refresh").enable();
					e.updateInfo();
					e.fireEvent("change", e, d);
				},
				resetTool: function(value){
					var pageCount = this.getPageData().pageCount;
					this.child('#last').setDisabled(value == pageCount || pageCount == 1);
				    this.child('#next').setDisabled(value == pageCount || pageCount == 1);
				    this.child('#first').setDisabled(value <= 1);
				    this.child('#prev').setDisabled(value <= 1);
				}
    	};
    }
});