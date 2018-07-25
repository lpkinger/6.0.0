Ext.define('erp.view.core.button.UseableReplace',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUseableReplaceButton',
		text: $I18N.common.button.erpUseableReplaceButton,
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpUseableReplaceButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 100,
		requires:['erp.view.pm.mps.MrpReplaceGrid'],
		handler:function(){
			var lastselected=Ext.getCmp('batchDealGridPanel').selModel.lastSelected;
			var formvalues=Ext.getCmp('dealform').getValues();
			if(!lastselected){
				Ext.Msg.alert('提示','请选择查看行');
			}
			var value=lastselected.data.ad_mdid;
			if(lastselected){
				Ext.create('Ext.window.Window', {
				    title: '可用替代料',
				    height:400,
				    width: 908,
				    id:'win',
				    layout: 'fit',
				    items: {
				        xtype: 'MrpReplaceGrid',
				        border: false,
				        caller:'ApplicationReplace!MRP',
				        condition:"ar_mdid="+value,
				        id:'replacegrid',
				        cls:'x-replacegrid-view',
				        multiselected:new Array(),
				        plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				            clicksToEdit: 1
				        })],
				        getMultiSelected: function(){
				    		var grid = this;
				            var items = grid.selModel.getSelection();
				            Ext.each(items, function(item, index){
				            	if(this.data.mr_id != null && this.data.mr_id != ''
				            		&& this.data.mr_id != '0' && this.data.mr_id != 0&&this.data.mr_realqty!='0'&& this.data.mr_realqty != ''){
				            		grid.multiselected.push(item);
				            	}
				            });
				    		return this.unique(grid.multiselected);
				    	},
				    	unique: function(items) {
				    		var d = new Object();
				    		Ext.Array.each(items, function(item){
				    			d[item.id] = item;
				    		});
				    		return Ext.Object.getValues(d);
				    	},
				        selModel: Ext.create('Ext.selection.CheckboxModel',{
					    	ignoreRightMouseSelection : false,
							listeners:{
					            selectionchange:function(selectionModel, selected, options){
					          
					            }
					        },
					        getEditor: function(){
					        	return null;
					        },
					        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
					        	var me = Ext.getCmp('replacegrid');
					        	var bool = true;
					        	var items = me.selModel.getSelection();
					            Ext.each(items, function(item, index){
					            	if(this.index == record.index){
					            		bool = false;
					            		me.selModel.deselect(record);
					            		Ext.Array.remove(items, item);
					            		Ext.Array.remove(me.multiselected, record);
					            	}
					            });
					            Ext.each(me.multiselected, function(item, index){
					            	items.push(item);
					            });
					            me.selModel.select(items);
					        	if(bool){
					        		view.el.focus();
						        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
						        	if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
						        		me.multiselected.push(record);
						        		items.push(record);
						        		me.selModel.select(items);
						        	} else {
						        		me.selModel.deselect(record);
						        		Ext.Array.remove(me.multiselected, record);
						        	}
					        	}					        	
					        },
					        onHeaderClick: function(headerCt, header, e) {
					            if (header.isCheckerHd) {
					                e.stopEvent();
					                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
					                if (isChecked) {
					                    this.deselectAll(true);
					                    var grid = Ext.getCmp('replacegrid');
					                    this.deselect(grid.multiselected);
					                    grid.multiselected = new Array();
					                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
						                Ext.each(els, function(el, index){
						                	el.setAttribute('class','x-grid-row-checker');
						                });
					                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
					                } else {
					                	var grid = Ext.getCmp('replacegrid');
					                	this.deselect(grid.multiselected);
						                grid.multiselected = new Array();
						                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
						                Ext.each(els, function(el, index){
						                	el.setAttribute('class','x-grid-row-checker');
						                });
					                    this.selectAll(true);
					                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
					                }
					            }					            
					        }
					})
				    },
				    buttonAlign:'center',
				    buttons:[{
				    	xtype:'button',
				    	iconCls: 'x-button-icon-turn',
				    	cls: 'x-btn-gray',
				    	text:'下达',
				    	handler:function(btn){
				    		var records=Ext.getCmp('replacegrid').getMultiSelected();
				    		var data = new Array();
							var bool = false;
							var grid = Ext.getCmp('replacegrid');
				    		Ext.each(records, function(record, index){
								if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
					        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0)){
									bool = true;
									var o = new Object();
									if(grid.keyField){
										o[grid.keyField] = record.data[grid.keyField];
									} else {
										params.id[index] = record.data[form.fo_detailMainKeyField];
									}
									if(grid.toField){
										Ext.each(grid.toField, function(f, index){
											var v = Ext.getCmp(f).value;
											if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
												o[f] = v;
											}
										});
									}
									if(grid.necessaryFields){
										Ext.each(grid.necessaryFields, function(f, index){
											var v = record.data[f];
											if(Ext.isDate(v)){
												v = Ext.Date.toString(v);
											}
											o[f] = v;
										});
									}
									data.push(o);
								}
							});
				    		
				    		if(bool){
				    			//下达操作
				    			var qty=lastselected.data.ad_qty;
				    			var yqty=lastselected.data.ad_yqty;
				    			var realcount=0;
				    			for(var i=0;i<data.length;i++){
				    				realcount+=data.mr_realqty;
				    			}
				    			if(realcount>(qty-yqty)){
				    				Ext.Msg.alert('提示','下达总数不能超过'+(qty-yqty));
				    			}else {
				    			var params = new Object();
				    			params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
				    			params.purchasecode=formvalues.pu_code;
				    			params.apdata=unescape(Ext.JSON.encode(lastselected.data).replace(/\\/g,"%"));
;				    			Ext.Ajax.request({
				    			 	method:'post',
				    			 	params:params,
				    			 	url:basePath+'pm/mps/turnReplaceProd.action',
				    			 	callback : function(options,success,response){
							   			var localJson = new Ext.decode(response.responseText);
							   			if(localJson.exceptionInfo){
							   				var str = localJson.exceptionInfo;
							   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
							   					str = str.replace('AFTERSUCCESS', '');
							   					grid.multiselected = new Array();
							   					Ext.getCmp('dealform').onQuery();
							   				}
							   				showError(str);return;
							   			}
						    			if(localJson.success){
						    				if(localJson.log){
						    					showMessage("提示", localJson.log);  
						    					Ext.getCmp('dealform').onQuery();
							   					btn.ownerCt.ownerCt.close(); 
						    				} 
							   			}
							   		}
				    				
				    			});
				    		}
				    		}
				    	}    	
				      },{
				    	xtype:'button',
				    	text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-button-icon-close',
				    	cls: 'x-btn-gray',
				    	width: 60,
				    	style: {
				    		marginLeft: '10px'
				        },
				        handler:function(btn){
				        	btn.ownerCt.ownerCt.close();
				        }
				    }]
				}).show();
			}else{
			 Ext.Msg.alert('提示','请先选择查看记录或选中记录不存在替代料!');	
		   }
		}
	});