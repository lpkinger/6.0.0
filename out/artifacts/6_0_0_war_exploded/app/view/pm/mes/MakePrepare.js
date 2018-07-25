Ext.define('erp.view.pm.mes.MakePrepare',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'pm/mes/saveMakePrepare.action',
				updateUrl: 'pm/mes/updateMakePrepare.action',
				deleteUrl:'pm/mes/deleteMakePrepare.action',
				getIdUrl: 'common/getId.action?seq=MakePrepare_SEQ',
				submitUrl: 'pm/mes/submitMakePrepare.action',
				resSubmitUrl: 'pm/mes/resSubmitMakePrepare.action',
				auditUrl: 'pm/mes/auditMakePrepare.action',
				resAuditUrl: 'pm/mes/resAuditMakePrepare.action',			
				keyField: 'mp_id',
				codeField: 'mp_code', 
				statusField: 'mp_status',
				statuscodeField: 'mp_statuscode'
			},{
				xtype:'tabpanel',
				anchor: '100% 55%', 
				items:[{					
					anchor: '100% 55%',
					xtype: 'erpGridPanel2',
					title:'已备料料卷列表',
					id: 'grid',
					mainField:'md_mpid',
					keyField:'mp_id',
					headerCt: Ext.create("Ext.grid.header.Container",{
						 forceFit: false,
						 sortable: true,
						 enableColumnMove:true,
						 enableColumnResize:true,
						 enableColumnHide: true
					}),			
				    plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu'),Ext.create('erp.view.core.grid.HeaderFilter')],
					invalidateScrollerOnRefresh: false,	
					listeners: {
					    'headerfiltersapply': function(grid, filters) {
					        if(this.allowFilter){
					        	var condition = null;
					            for(var fn in filters){
					                var value = filters[fn],f = grid.getHeaderFilterField(fn);
					                if(!Ext.isEmpty(value)){
					                   if(f.filtertype) {
					                      if (f.filtertype == 'numberfield') {
					                    	 value = fn + "=" + value + " ";
					                      }
					                    } else {
					                    	if(Ext.isDate(value)){
					                        	value = Ext.Date.toString(value);
					                        	value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
					                        } else {
					                        	var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
					                        	exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
					    	                    if(exp_d.test(value)){
					    	                    	value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
					    	                    } else if(exp_t.test(value)){
					    	                    	value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
					    	                    } else{
					    	                    	if (f.xtype == 'combo' || f.xtype == 'combofield') {
					    	                    		if (value == '-所有-') {
					    	                    			continue;
					    	                    		} else if (value == '-无-') {
					    	                    			value = 'nvl(' + fn + ',\' \')=\' \'';
					    	                    		} else {
					    	                    			value = fn + " LIKE '" + value + "%' ";
					    	                    		}
					    	                    	} else {
					    	                    		//**字符串转换下简体*//*
					    	                    		var SimplizedValue=this.BaseUtil.Simplized(value);   	                    	
					    	                    		//可能就是按繁体筛选  
					    	                    		if(f.ignoreCase) {// 忽略大小写
					        	                    		fn = 'upper(' + fn + ')';
					        	                    		value = value.toUpperCase();
					        	                    	}
					        	                    	if(!f.autoDim) {
					        	                    		if(SimplizedValue!=value){
					        	                    			value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
					        	                    		}else value = fn + " LIKE '" + value + "%' ";       	                    						        	                    			
					        	                    	} else if(f.exactSearch){
					        	                    		value=fn+"='"+value+"'";
					        	                    	}else {
					        	                    		if(SimplizedValue!=value){
					        	                    			value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
					        	                    		}else value = fn + " LIKE '%" + value + "%' ";       	                    			        	                    			
					        	                    	}
					    	                    	}
					    	                    }
					                        }
					                    }
					                    if(condition == null){
					                    	condition = value;
					                    } else {
					                    	condition = condition + " AND " + value;
					                    }
					                  }
					               }
					               this.filterCondition = condition;
					               var grid=Ext.getCmp('grid');
					               if(grid.store.data!=grid.store.prefetchData){
					                grid.store.loadData(grid.store.prefetchData.items);
					               }
					        	} else {
					        		this.allowFilter = true;
					        	}
					        	return false;
					        }
					    },
					    viewConfig: {
					        trackOver: false
					    },
					    buffered: true,
					    sync: true												
				},{
					items: [],
					title:'备料清单',
					layout: 'anchor',
					id: 'tab-list'
				}]
			},{
				xtype: 'form',
				anchor: '100% 20%',
				bodyStyle: 'background: #f1f1f1;',
				layout: 'border',
				items: [{
					xtype: 'fieldcontainer',
					region: 'center',
					autoScroll: true,
					scrollable: true,
					defaults: {
						width: 300
					},
					items: [{
						xtype: 'textfield',
						fieldLabel: '操作人员',
						readOnly:true,
						id:'man',
						allowBlank: false,
						value: em_name
					},{
						xtype: 'fieldcontainer',
						fieldLabel : '操作', 
						defaultType: 'radiofield',
						layout: 'hbox',
						colspan: 2,
						defaults: {
			                flex: 1
			            },
			            items: [{
			            	boxLabel  : '备料',
			                inputValue: 'get',
			                name: 'operator',
			                id        : 'get',
			                checked: true
			            }, {
			                boxLabel  : '退回',
			                inputValue: 'back',
			                name: 'operator',
			                id        : 'back'
			            }]
					},{
						xtype: 'textfield',
						fieldLabel: '料卷编号',
						readOnly:false,
						id:'code',
						colspan: 1,
						emptyText: '请采集料卷编号',
						allowBlank: false
					},{
						xtype: 'textfield',
						fieldLabel: '数量',
						readOnly:true,
						colspan: 1,
						id:'qty'
					}]
				},{
					xtype: 'dataview',
					region : 'east',
					width: 500,
					id: 't_result',
					store: new Ext.data.Store({
						fields: ['type', 'text']
					}),
					cls: 'msg-body',
				    tpl: new Ext.XTemplate(
				    	'<audio id="audio-success" src="' + basePath + 'resource/audio/success.wav"></audio>',
				    	'<audio id="audio-error" src="' + basePath + 'resource/audio/error.wav"></audio>',
				    	'<tpl for=".">',
				            '<div class="msg-item">',
				            	'<tpl if="type == \'success\'"><span class="text-info">{text}</span></tpl>',
				            	'<tpl if="type == \'error\'"><span class="text-warning">{text}</span></tpl>',
				            '</div>',
				        '</tpl>'
				    ),
				    itemSelector: 'div.msg-item',
				    emptyText: '提示信息',
				    deferEmptyText: false,
				    autoScroll: true,
				    append: function(text, type) {
				    	type = type || 'success';
				    	this.getStore().add({text: text, type: type});
				    	this.getEl().scroll("b", this.getEl().getHeight(), true);  
				    	var el = Ext.get('audio-' + type).dom;
				    	el.play();
				    }
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'button',
					id : 'confirm',
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-gray',
					style: {
			    		marginLeft: '10px'
			        },
					width: 80
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});