Ext.define('erp.view.fa.CheckAccountSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				xtype: 'form',
				anchor: '100% 20%',
				title:'期末对账设置',
				frame : true,
				layout: {
			        type: 'hbox',
			        align: 'middle'
				},
				autoScroll : true,
				defaultType : 'textfield',
				labelSeparator : ':',
				buttonAlign : 'center',
				cls: 'u-form-default',
				fieldDefaults : {
					fieldStyle : 'background:#FFFAFA;color:#515151;',
					focusCls: 'x-form-field-cir-focus',
					labelAlign : 'right',
					msgTarget: 'side',
					blankText : $I18N.common.form.blankText
				},
				items: [{	
					xtype:'combo',
					fieldLabel:'模块',
					columnWidth:0.25,
					id:'module',
					name:'module',
					store:Ext.create('Ext.data.Store', {
					    fields: ['display', 'value'],
					    data : [
					        {display:'应收', value:'AR'},
					        {display:'应付', value:'AP'},
					        {display:'总账', value:'GL'},
					        {display:'成本', value:'CP'},
					        {display:'固定资产', value:'AS'},
					        {display:'票据资金', value:'CB'},
					        {display:'库存结账', value:'ST'},
					        {display:'库存冻结', value:'STF'},
					        {display:'成本计算前', value:'CPB'},
					        {display:'成本计算后', value:'CPA'},
					        {display:'核算前检测', value:'STB'}
					    ]
					}),
					queryMode: 'local',
    				displayField: 'display',
    				valueField: 'value',
					value:'AR'
				}],
				buttons:[{
					xtype:'erpUpdateButton'
				},{
					xtype:'erpCloseButton'
				}]
			},{
				xtype:'gridpanel',
				anchor: '100% 80%',
				layout : 'fit',
				id:'mainset',
				plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit : 1
			    })],
				autoScroll : true,
			    columnLines : true,
			    store: Ext.create('Ext.data.Store',{
					fields:['enable_','code_','module_','detno_','title_','execute_','billoutmode_','man_','date_']
				}),
				columns:[
					{
						header:'启用状态',
						dataIndex:'enable_',
						width:65,
						cls : 'x-grid-header-1',
						xtype:'actioncolumn',
						processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
					        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
					        	var record = null;
					        	var dataIndex = this.dataIndex;
					        	var checked = null;					        	
				        		record = view.panel.store.getAt(recordIndex);
				        		checked = !record.get(dataIndex);
					            record.set(dataIndex, checked);
					            this.fireEvent('checkchange', this, recordIndex, checked);
					            return false;
					        }
					    },
						renderer:function(value, m, record){
					        var cssPrefix = Ext.baseCSSPrefix,
					            cls = [cssPrefix + 'grid-enableheader'];
					        if (value) {
					            cls.push(cssPrefix + 'grid-enableheader-checked');
					        }
					        return '<div class="' + cls.join(' ') + '">&#160;</div>';
					    }
					},{
						header:'检测号',
						dataIndex:'code_',
						cls : 'x-grid-header-1',
						width:100,
						hidden:true
					},{
						header:'所属模块',
						dataIndex:'module_',
						cls : 'x-grid-header-1',
						width:100,
						hidden:true
					},{
						header:'序号',
						dataIndex:'detno_',
						align:'center',
						cls : 'x-grid-header-1',
						style:'color:#FF0000',
						necessField:true,
						width:65,
						editor:{
							xtype:'textfield'
						}
					},{
						header:'检测项描述',
						dataIndex:'title_',
						style:'color:#FF0000',
						cls : 'x-grid-header-1',
						necessField:true,
						width:250,
						editor:{
							xtype:'textareatrigger'
						},
						renderer:function(val, meta, record, x, y, store, view){
						 	var grid = view.ownerCt,column = grid.columns[y];
						 	meta.style="padding-right:0px!important";
						 	meta.tdAttr = 'data-qtip="' + Ext.String.htmlEncode(val) + '"';  
						 	if(val){
						 		return  '<span style="display:inline-block;padding-left:2px;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+Ext.String.htmlEncode(val)+'</span>'+
						 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;"' +
						 				'onClick="Ext.getCmp(\'mainset\').showTrigger(' + '\''+escape(val)+'\',\''+column.dataIndex+'\','+x+','+y+');"></span>';
						 	}
						 	return '';
						 }
					},{
						header:'取数SQL',
						dataIndex:'execute_',
						cls : 'x-grid-header-1',
						width:500,
						renderer:function(val, meta, record, x, y, store, view){
						 	var grid = view.ownerCt,column = grid.columns[y];
						 	meta.style="padding-right:0px!important";
						 	if(val){
						 		return  '<span style="display:inline-block;padding-left:2px;width:95%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+Ext.String.htmlEncode(val)+'</span>'+
						 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;"' +
						 				'onClick="Ext.getCmp(\'mainset\').showTrigger(' + '\''+escape(val)+'\',\''+column.dataIndex+'\','+x+','+y+');"></span>';
						 	
						 	}
						 	return '';
						},
						editor:{
							xtype:'textareatrigger'
						}
					},{xtype:'actioncolumn',
						align:'center',
						header:'设置',
						id:'set',
						width:80,
						cls : 'x-grid-header-1',
						items:[{							
							tooltip:'参数设置',
							align:'center',
							id:'paramset_',
							icon : basePath + 'resource/images/set/errorset.png',
							getClass:function(v,meta,r,rowIndex,colIndex,store){
								return 'paramset';
							},
							handler: function(grid, rowIndex, colIndex, item) {  
		                       var rec = grid.getStore().getAt(rowIndex);  
		                       this.fireEvent('paramset',  grid, rec);  
		                   }  
						},{							
							tooltip:'错误输出设置',
							align:'center',
							id:'errorset_',
							icon : basePath + 'resource/images/16/edit.png',
							getClass:function(v,meta,r,rowIndex,colIndex,store){
								return 'errorset';
							},
							handler: function(grid, rowIndex, colIndex, item) {  
		                       var rec = grid.getStore().getAt(rowIndex);  
		                       this.fireEvent('errorset', grid, rec);  
		                   }  
						}]
					},{
						header:'开票模式',
						xtype:'combocolumn',
						dataIndex:'billoutmode_',
						cls : 'x-grid-header-1',
						width:120,
						editor:{
							xtype:'combo',
							editable:false,
							store:{
							    fields: ['display', 'value'],
							    data : [
							        {display:'全部', value:'all'},
					        		{display:'应收开票记录', value:'useBillOutAR'},
					        		{display:'应收非开票记录', value:'notBillOutAR'},
					        		{display:'应付开票记录', value:'useBillOutAP'},
					        		{display:'应付非开票记录', value:'notBillOutAP'}
							    ]
							},
							queryMode: 'local',
		    				displayField: 'display',
		    				valueField: 'value',
		    				listeners:{
		    					change:function(combo, newValue, oldValue){
		    						var module = Ext.getCmp('module').value;
		    						if(oldValue&&module=='AR'&&(newValue=='useBillOutAP'||newValue=='notBillOutAP')){
		    							showError('应收模块，不能选择应付开票记录、应付非开票记录模式！');
		    							combo.setValue(newValue.replace('AP','AR'));
		    						}
		    						if(oldValue&&module=='AP'&&(newValue=='useBillOutAR'||newValue=='notBillOutAR')){
		    							showError('应付模块，不能选择应收开票记录、应收非开票记录模式！');
		    							combo.setValue(newValue.replace('AR','AP'));
		    						}
		    					}
		    				}
						}
					},{
						header:'最近更新人',
						dataIndex:'man_',
						cls : 'x-grid-header-1',
						width:100,
						hidden:true
					},{
						header:'最近更新日期',
						dataIndex:'date_',
						cls : 'x-grid-header-1',
						width:100,
						hidden:true
					}],
				showTrigger:function(val,name,x,y){//明细行文本框
		        	val = unescape(val);
					var record = this.store.getAt(x);
					Ext.MessageBox.minPromptWidth = 600;
			        Ext.MessageBox.defaultTextHeight = 200;
			        Ext.MessageBox.style= 'background:#e0e0e0;';
			        Ext.MessageBox.prompt("详细内容", '',
			        function(btn, text) { 
				        if (btn == 'ok') {
			                if (name&&record) {
			                    record.set(name, text);
			                }
			            }
		            },
			        this, true, //表示文本框为多行文本框    
			        val);
				}
			}]
			
		}); 
		me.callParent(arguments); 
	} 
});