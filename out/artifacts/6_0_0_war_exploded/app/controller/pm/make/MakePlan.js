Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakePlan', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
     		'pm.make.MakePlan','common.datalist.GridPanel','common.datalist.Toolbar','core.button.VastAudit','core.button.VastDelete',
     		'core.button.VastPrint','core.button.VastReply','core.button.VastSubmit','core.button.ResAudit','core.form.FtField',
	       'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField','core.form.BtnDateField',
	       'core.form.FtNumberField', 'core.form.MonthDateField','common.batchDeal.Form','core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.DbfindTrigger','core.form.ConDateField'
     	],
    init:function(){
        this.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.FormUtil = Ext.create('erp.util.FormUtil');
        this.RenderUtil=Ext.create('erp.util.RenderUtil');
    	this.control({
    		'erpDatalistGridPanel': { 
    			itemclick: this.onGridItemClick,
    			afterrender:function(grid){
    				if(Ext.isIE && !Ext.isIE11){
    					document.body.attachEvent('onkeydown', function(){
    						if(window.event.ctrlKey && window.event.keyCode == 67){//Ctrl + C
    							var e = window.event;
    							if(e.srcElement) {
    								window.clipboardData.setData('text', e.srcElement.innerHTML);
    							}
    						}
    					});
    				} else {
    					grid.getEl().dom.addEventListener("mouseover", function(e){
        					if(e.ctrlKey){
        						 var Contextvalue=e.target.textContent==""?e.target.value:e.target.textContent;
        						 textarea_text = parent.document.getElementById("textarea_text");
        						 textarea_text.value=Contextvalue;
        					     textarea_text.focus();
        					     textarea_text.select();
        					}
        		    	});
    				}
    			}
    		},
    		'erpVastDeleteButton': {
    			click: function(btn){
    				var dlwin = new Ext.window.Window({
   			    		id : 'dlwin',
	   				    title: btn.text,
	   				    height: "100%",
	   				    width: "80%",
	   				    maximizable : true,
	   					buttonAlign : 'center',
	   					layout : 'anchor',
	   				    items: [{
	   				    	  tag : 'iframe',
	   				    	  frame : true,
	   				    	  anchor : '100% 100%',
	   				    	  layout : 'fit',
	   				    	  html : '<iframe id="iframe_dl_'+caller+'" src="'+basePath+'jsps/common/vastDatalist.jsp?urlcondition='+condition+'&whoami='+caller+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	   				    }],
	   				    buttons : [{
	   				    	text: btn.text,
	   				    	iconCls: btn.iconCls,
	   				    	cls: 'x-btn-gray-1',
	   				    	handler: function(){
	   				    		
	   				    	}
	   				    },{
	   				    	text : '关  闭',
	   				    	iconCls: 'x-button-icon-close',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(){
	   				    		Ext.getCmp('dlwin').close();
	   				    	}
	   				    }]
	   				});
	   				dlwin.show();
    			}
    		},
    		'button[id=query]':{
      		  beforerender:function(btn){
      			btn.handler=function(){
      				var form=btn.ownerCt.ownerCt;
      				if(form){
      					var condition=form.getCondition();
      					if(!Ext.isEmpty(condition)) {
      						var grid=Ext.getCmp('grid');
      						grid.getCount(caller,condition);
      					}
      				}
      			};
      		 }
      		},
    		'button[id=searchlist]': {
    			beforerender:function(btn){
    				btn.hidden=true;
    			},
    			//searchlist存在很多问题
    			click: function(){
    				this.showSearchListWin();
    			}
    		},
    		'button[id=customize]': {
    			   click: function(){
    				   this.showCustomizeWin();
    			   }
    		  },
    		'button[id=relativelist]':{
    			beforerender:function(btn){
    				btn.hidden=true;
    			}
    		},
    		'button[id=export]':{ //该页面的从表用的是datalist，导出按钮没有意义
      		  afterrender:function(btn){
      			btn.hidden=true;
      		 }
      		},
    		'dbfindtrigger[name=sl_label]': {
    			afterrender: function(t){
    				t.dbBaseCondition = 'sl_caller=\'' + caller + '\'';
    			}
    		}
    	});
    }, 
    showCustomizeWin:function(){
    	 var me = this, win = this.CustomizeWin, grid = Ext.getCmp('grid');
  	   if(!win){
  		   var ablecolumns=new Array(),unselectcolumns=grid.basecolumns;
  		   Ext.Array.each(grid.columns,function(item){
  			   if(item.text && item.text.indexOf('&#160')<0){
  				   ablecolumns.push(item);
  			   }
  		   });
  		   unselectcolumns.splice(0,ablecolumns.length);
  		   this.CustomizeWin=win = Ext.create('Ext.window.Window', {
  			   title: '<div align="center">个性设置</div>',
  			   height: screen.height*0.7,
  			   width: screen.width*0.7*0.9,
  			   layout:'border',
  			   closeAction:'hide',
  			   items:[{
  				   region:'center',
  				   layout:{
  					   type: 'hbox',
  					   align: 'stretch',
  					   padding: 5
  				   },
  				   defaults     : { flex : 1 },
  				   items:[{
  					   xtype:'grid',
  					   multiSelect: true,
  					   id: 'fromgrid',
  					   title:'可选项',
  					   flex:0.7,
  					   cls: 'custom-grid',	    		
  					   store:Ext.create('Ext.data.Store', {
  						   fields: [{name:'dataIndex',type:'string'},{name:'text',type:'string'},{name:'width',type:'number'}],
  						   data: unselectcolumns,
  						   filterOnLoad: false 
  					   }),
  					   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
  					   viewConfig: {
  						   plugins: {
  							   ptype: 'gridviewdragdrop',
  							   dragGroup: 'togrid',
  							   dropGroup: 'togrid'
  						   }	    					
  					   },
  					   stripeRows: false,
  					   columnLines:true,
  					   columns:[{
  						   dataIndex:'dataIndex',
  						   cls :"x-grid-header-1",
  						   text:'字段名称',
  						   width:120,
  						   filter: {
  							   xtype : 'textfield'
  						   }
  					   },{
  						   dataIndex:'text',
  						   text:'描述',
  						   cls :"x-grid-header-1",
  						   flex:1,
  						   filter: {
  							   xtype : 'textfield'
  						   }
  					   },{
  						   dataIndex:'width',
  						   text:'宽度',
  						   width:60,
  						   cls :"x-grid-header-1",
  						   align:'right',
  						   editor: {
  							   xtype: 'numberfield',
  							   format:0
  						   },
  						   filter: {
  							   xtype : 'textfield'
  						   }
  					   }]
  				   },{
  					   xtype:'grid',
  					   multiSelect: true,
  					   id: 'togrid',
  					   stripeRows: true,
  					   columnLines:true,
  					   title:'显示项',
  					   store:Ext.create('Ext.data.Store', {
  						   fields: [{name:'dataIndex',type:'string'},{name:'text',type:'string'},{name:'width',type:'number'},
  						            {name:'orderby',type:'string'},{name:'priority',type:'string'}],
  						            data:ablecolumns,
  						            filterOnLoad: false 
  					   }),
  					   necessaryField:'dataIndex',
  					   plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),
  					             Ext.create('Ext.grid.plugin.CellEditing', {
  					            	 clicksToEdit: 1
  					             })],
  					             viewConfig: {
  					            	 plugins: {
  					            		 ptype: 'gridviewdragdrop',
  					            		 dragGroup: 'togrid',
  					            		 dropGroup: 'togrid'
  					            	 }
  					             },
  					             columns:[{
  					            	 dataIndex:'dataIndex',
  					            	 text:'字段名称',
  					            	 cls :"x-grid-header-1",
  					            	 width:120,
  					            	 filter: {
  					            		 xtype : 'textfield'
  					            	 }
  					             },{
  					            	 dataIndex:'text',
  					            	 text:'描述',
  					            	 cls :"x-grid-header-1",
  					            	 flex:1,
  					            	 filter: {
  					            		 xtype : 'textfield'
  					            	 }
  					             },{
  					            	 dataIndex:'width',
  					            	 text:'宽度',
  					            	 width:60,
  					            	 xtype:'numbercolumn',
  					            	 align:'right',
  					            	 cls :"x-grid-header-1",
  					            	 filter: {
  					            		 xtype : 'textfield'
  					            	 },
  					            	 editable:true,
  					            	 format: '0',
  					            	 editor: {
  					            		 xtype: 'numberfield',
  					            		 hideTrigger: true
  					            	 },
  					             },
  					             {
  					            	 dataIndex:'orderby',
  					            	 text:'排序',
  					            	 width:60,
  					            	 xtype:'combocolumn',
  					            	 cls :"x-grid-header-1",
  					            	 filter: {
  					            		 xtype : 'textfield'
  					            	 },
  					            	 renderer:function(val){
  					            		 if(val=='ASC'){
  					            			 return '<img src="' + basePath + 'resource/images/16/up.png">' + 
  					            			 '<span style="color:red;padding-left:2px">升序</span>';
  					            		 } else if(val=='DESC') {
  					            			 return '<img src="' + basePath + 'resource/images/16/down.png">' + 
  					            			 '<span style="color:red;padding-left:2px">降序</span>';
  					            		 }
  					            	 },
  					            	editor:{
  					            			 xtype:'combo',
  					            			 queryMode: 'local',
  					            			 displayField: 'display',
  					            			 valueField: 'value',
  					            			 store:Ext.create('Ext.data.Store', {
  					            				 fields: ['value', 'display'],
  					            				 data : [{value:"ASC", display:"升序"},
  					            				         {value:"DESC", display:"降序"}]
  					            			 })
  					            		 }
  					            	 },{
  					            		 dataIndex:'priority',
  					            		 text:'优先级',
  					            		 width:60,
  					            		 align:'right',
  					            		 cls :"x-grid-header-1",
  					            		 filter: {
  					            			 xtype : 'textfield'
  					            		 },
  					            		 editor:{
  					            			 xtype:'combo',
  					            			 queryMode: 'local',
  					            			 displayField: 'display',
  					            			 valueField: 'value',
  					            			 store:Ext.create('Ext.data.Store', {
  					            				 fields: ['value', 'display'],
  					            				 data : [{value:"1", display:"1"},
  					            				         {value:"2", display:"2"},
  					            				         {value:"3", display:"3"},
  					            				         {value:"4", display:"4"},
  					            				         {value:"5", display:"5"},
  					            				         {value:"6", display:"6"},
  					            				         {value:"7", display:"7"},
  					            				         {value:"8", display:"8"},
  					            				         {value:"9", display:"9"},]
  					            			 })
  					            		 }
  					            	 }] 

  					             }]
  				   }],
  				   buttonAlign:'center',
  				   buttons:['->',{
  					   text:'重置',
  					   scope:this,
  					   handler:function(btn){
  						   warnMsg('重置列表将还原配置，确认重置吗?', function(btn){
  								if(btn == 'yes'){
  									Ext.Ajax.request({
  										url : basePath + 'common/resetEmpsDataListDetails.action',
  										params: {
  											caller:caller
  										},
  										method : 'post',
  										callback : function(options,success,response){
  											var localJson = new Ext.decode(response.responseText);
  											if(localJson.success){
  												showMessage('提示','重置成功!',1000);
  													window.location.reload();
  											} else {
  												if(localJson.exceptionInfo){
  													var str = localJson.exceptionInfo;
  													if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
  														str = str.replace('AFTERSUCCESS', '');
  														showError(str);
  													} else {
  														showError(str);return;
  													}
  												}
  											}
  										}
  									});
  								}
  							});
  					   }
  				    },{
  					   style:'margin-left:5px;',
  					   text:'保存',
  					   scope:this,
  					   handler:function(btn){
  						   var grid=Ext.getCmp('togrid'),fromgrid=Ext.getCmp('fromgrid');
  						   var jsonGridData = new Array(),datas=new Array();
  							var form = Ext.getCmp('form');
  							grid.getStore().each(function(item){//将grid里面各行的数据获取并拼成jsonGridData
  								var data = {
  								  dde_field:item.data.dataIndex,
  								  dde_width:item.data.width,
  								  dde_orderby:item.data.orderby,
  								  dde_priority:item.data.priority
  								}; 
  								item.dirty=false;
                              	jsonGridData.push(Ext.JSON.encode(data));
                              	datas.push(item.data);
  							});
  						   Ext.Ajax.request({
  								url : basePath + 'common/saveEmpsDataListDetails.action',
  								params : {
  									caller:caller,
  									data:unescape(jsonGridData.toString())
  								},
  								method : 'post',
  								callback : function(options,success,response){
  									var localJson = new Ext.decode(response.responseText);
  									if(localJson.success){
  										showMessage('提示','保存成功!',1000);
  										window.location.reload();
  									}
  								}

  							});
  						   
  					   }
  				   },{
  					   style:'margin-left:5px;',
  					   text:'关闭',
  					   handler:function(btn){
  						   btn.ownerCt.ownerCt.hide();
  					   }
  				   },'->']

  			   });
  		   }
  		   win.show();
    },
    onGridItemClick: function(selModel, record){
    	//打开小窗口 编辑  分拆工单
    	var me = this,ma_code=record.data.ma_code;
    	Ext.create('Ext.window.Window',{
    		width:850,
    		height:'80%',
    		iconCls:'x-grid-icon-partition',
    		title:'<h1>工单拆分</h1>',
    		id:'win',
    		items:[{
    			xtype:'form',
    			layout:'column',
    			region:'north',
    			frame:true,
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.5,
    				readOnly:true,
    				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
    			},
    			items:[{
    			 fieldLabel:'制造单号',
    			 value:record.data.ma_code,
    			 id:'macode'
    			},{
    			 fieldLabel:'物料编号'	,
    			 value:record.data.ma_prodcode
    			},{
    			 fieldLabel:'物料名称',
    			 value:record.data.pr_detail
    			},{
    			 fieldLabel:'订单编号'	,
    			 value:record.data.ma_salecode
    			},{
    		     fieldLabel:'订单序号',
    		     value:record.data.ma_saledetno
    			},{
    			  fieldLabel:'制单数量',
    			  value:record.data.ma_qty,
    			  id:'ma_qty'
    			},{
    			  fieldLabel:'已完工数',
    			  value:record.data.ma_madeqty,
    			  id:'ma_madeqty'
    			},{
    			  fieldLabel:'拼板数',
    			  value:record.data.pr_combineqty,
    			  id:'pr_combineqty'
    			}]
    		},{
    			xtype:'form',
    			layout:'column',
    			frame:true,
    			id:'planform',
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.26,
    				readOnly:false,
    				labelAlign:'right',
    				fieldStyle:'background:#FFFAFA;color:#515151;'
    			},
    			items:[{
    			 fieldLabel:'计划开工日期',
    			 xtype:'datefield',
    			 id:'ma_planbegindate',
    			 name:'ma_planbegindate',
    			 value:record.data.ma_planbegindate
    			},{
    			 fieldLabel:'计划完工日期',
       			 xtype:'datefield',
       			id:'ma_planenddate',
       		    name:'ma_planenddate',
       			value:record.data.ma_planenddate
    			},{
    			 fieldLabel:'线别',
    			 id:'ma_teamcode',
    			 name:'ma_teamcode',
    			 labelWidth:50,
    			 columnWidth:0.2,
    			 xtype:'dbfindtrigger',
    			 value:record.data.ma_teamcode
    			 },{
    				id:'ma_id',
    				xtype:'hidden',
    				name:'ma_id',
    				value:record.data.ma_id
    			 },{
    				xtype:'button',
    				columnWidth:0.12,
    				text:'保存',
    				width:60,
    				iconCls: 'x-button-icon-save',
    				margin:'0 0 0 30',
    				handler:function(btn){
    				   var store=Ext.getCmp('smallgrid').getStore();
    				   var count=0;
    				   var jsonData=new Array();
    				   var pr_combineqty = Ext.getCmp('pr_combineqty');
    				   var dd,version;
    				   var flag = false;
    				   Ext.Array.each(store.data.items,function(item){
    					  if(item.data.ma_planbegindate!=null&&item.data.ma_qty>0){
    						  version=item.data.ma_version;
    						  if(pr_combineqty&&pr_combineqty.value>0){
    						      var result = Number(item.data.ma_qty)%Number(pr_combineqty.value);
    						      if(result>0){
    							      flag = true;
    								  return;
    							  }
    						  }
    						  if(!version){
    							  dd=new Object();
    							  //说明是新增批次 
    							  dd['ma_planbegindate']=Ext.Date.format(item.data.ma_planbegindate, 'Y-m-d');
    							  if(item.data.ma_planenddate!=null){
    								  dd['ma_planenddate']=Ext.Date.format(item.data.ma_planenddate, 'Y-m-d');
    							  }else{
    								  dd['ma_planenddate']=null;
    							  }
    								  
    							  dd['ma_qty']=item.data.ma_qty;  
    							  dd['ma_teamcode']=item.data.ma_teamcode;
    							  jsonData.push(Ext.JSON.encode(dd));
    							  count+=Number(item.data.ma_qty);
    						  }
    						 
    					  }
    				   });
    				   if(flag){
    					   showError("新拆分数量必须是拼板数的整数倍!");
    					   return;
    				   }
    				   var r=Ext.getCmp('planform').getValues();
    				   var params=new Object();
    				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    				   params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
    				   var assqty=Number(record.data.ma_qty)-Number(record.data.ma_madeqty);
    				   if(count>assqty){
    					showError('分拆数量不能大于未完工数!') ;  
    					return;
    				   }else{
    					   Ext.Ajax.request({
    					   	  url : basePath +'pm/make/splitMake.action',
    					   	  params : params,
    					   	  method : 'post',
    					   	  callback : function(options,success,response){
    					   		var localJson = new Ext.decode(response.responseText);
    					   		if(localJson.success){
    			    				saveSuccess(function(){
    			    					//add成功后刷新页面进入可编辑的页面 
    			    					Ext.create('erp.util.GridUtil').loadNewStore(Ext.getCmp('smallgrid'),{
    			    			    		caller:'MakeSplit',
    			    			    		condition:"ma_version='"+ma_code+"' order by ma_id desc"
    			    			    	});
    			    				});
    				   			} else if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					saveSuccess(function(){
    				    					//add成功后刷新页面进入可编辑的页面 
                                            Ext.create('erp.util.GridUtil').loadNewStore(Ext.getCmp('smallgrid'),{
        			    			    		caller:'MakeSplit',
        			    			    		condition:"ma_version='"+ma_code+"' order by ma_id desc"
        			    			    	});
    				    				});
    				   					showError(str);
    				   				} else {
    				   					showError(str);
    					   				return;
    				   				}
    					   			
    					   	 } else{
    				   				saveFailure();
    				   			}
    					   	  }
    					   });
    					   
    				   }
    				}
    			},{
    				xtype:'button',
    				columnWidth:0.1,
    				text:'关闭',
    				width:60,
    				iconCls: 'x-button-icon-close',
    				margin:'0 0 0 10',
    				handler:function(btn){
    					Ext.getCmp('win').close();
    				}
    			}]
    		},{
    		  xtype:'gridpanel',
    		  region:'south',
    		  id:'smallgrid',
    		  layout:'fit',
    		  height:0.45*(window.height),
    		  autoScroll:true,
    		  columnLines:true,
    		  store:Ext.create('Ext.data.Store',{
					fields:[{name:'ma_planbegindate',type:'date'},{name:'ma_planenddate',type:'date'},{name:'ma_teamcode',type:'string'},{name:'ma_qty',type:'int'},{name:'ma_code',type:'string'},{name:'ma_version',type:'string'}],
				    data:[]
    		  }),
    		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    		        clicksToEdit: 1,
    		        listeners:{
    		        	'edit':function(editor,e,Opts){
    		        		var record=e.record;
    		        		var version=record.data.ma_version;
    		        		if(version){
    		        			e.record.reject();
    		        		 Ext.Msg.alert('提示','不能修改已拆分工单!');
    		        		}
    		        		}
    		        	}
    		    })],
    		  tbar: [{
    			    tooltip: '添加批次',
    	            iconCls: 'x-button-icon-add',
    	            width:25,
    	            handler : function() {
    	            	var store = Ext.getCmp('smallgrid').getStore();
    	                var r = new Object();
    	                r.ma_planbegindate=null;
    	                r.ma_planenddate=null;
    	                r.ma_qty=null;
    	                r.ma_code=null;
    	                store.insert(store.getCount(), r);
    	            }
    	        }, {
    	            tooltip: '删除批次',
    	            width:25,
    	            itemId: 'delete',
    	            iconCls: 'x-button-icon-delete',
    	            handler: function(btn) {
    	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
    	                //ma_version 存在则不让删除
    	                var store=Ext.getCmp('smallgrid').getStore();
    	                store.remove(sm.getSelection());
    	                if (store.getCount() > 0) {
    	                    sm.select(0);
    	                }
    	            },
    	            disabled: true
    	        }],
    	      listeners:{
    	    	  itemmousedown:function(selmodel, record){
    	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
    	    	  } 
    	      }, 
    		  columns:[{
    			  dataIndex:'ma_planbegindate',
    			  header:'计划开工日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  },
    		  },{
    			  dataIndex:'ma_planenddate',
    			  header:'计划完工日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				  if(val)
					   return Ext.Date.format(val, 'Y-m-d');
				   else return null;
   			     },
   			     format:'Y-m-d',
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  }
    		  },{
    			  dataIndex:'ma_qty',
    			  header:'数量',
    			  width:120,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				   return val;
   			     },
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    			  dataIndex:'ma_teamcode',
    			  header:'线别',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				   return val;
   			     },
   			     dbfind:'MakeTeam|ct_varchar50_1',
    			 editor:{
    				  xtype:'dbfindtrigger',
    				  hideTrigger: false,
    				  name:'ma_teamcode',
    				  which:'grid',
    				  dbfind:'MakeTeam|ct_varchar50_1',
    				  listeners: {
                        aftertrigger: function(t, d) {
                        	var record = Ext.getCmp('smallgrid').selModel.lastSelected;  				
    				        record.set('ma_teamcode', d.data.CT_VARCHAR50_1);
                        }
                    }
    				  
    			  }
    		  },{
    			  dataIndex:'ma_code',
    			  header:'制造单号',
    			  width:150,
    			  //跳转链接的配置
    			  renderer:function(val,meta,record){
    			  	if(caller=='MakePlan'){
    			  		console.log("MakePlan");
    			  		return me.RenderUtil.MakePlanHref(val,meta,record);    			  		
    			  	}
    			  	if(caller=='MakePlan!OS'){
    			  		console.log("MakePlan!OS");
    			  		return me.RenderUtil.MakeHref(val,meta,record);
    			  	}
   			      }
    		  },{
    			  dataIndex:'ma_version',
    			  header:'原始工单',
    			  width:150,
    			  editable:false
    		  }]
    		}]
    		
    	}).show();
    	//给grid 赋值
    	 Ext.create('erp.util.GridUtil').loadNewStore(Ext.getCmp('smallgrid'),{
    		caller:'MakeSplit',
    		condition:"ma_version='"+ma_code+"' order by ma_id desc"
    	});
    	
    }, 
    openUrl: function(record) {
    	var me = this, value = record.data[keyField];
    	var formCondition = keyField + "IS" + value ;
    	var gridCondition = pfField + "IS" + value;
    	if(!Ext.isEmpty(pfField) && pfField.indexOf('+') > -1) {//多条件传入维护界面//vd_vsid@vd_id+vd_class@vd_class
    		var arr = pfField.split('+'),ff = [],k = [];
    		Ext.Array.each(arr, function(r){
    			ff = r.split('@');
    			k.push(ff[0] + 'IS\'' + record.get(ff[1]) + '\'');
    		});
    		gridCondition = k.join(' AND ');
    	}
    	var panelId = caller + keyField + "_" + value + gridCondition;
    	var panel = Ext.getCmp(panelId); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!main){
			main = parent.parent.Ext.getCmp("content-panel");
		}
    	if(!panel){ 
    		var title = "";
	    	if (value.toString().length>4) {
	    		 title = value.toString().substring(value.toString().length-4);	
	    	} else {
	    		title = value;
	    	}
	    	var myurl = '';
	    	if(me.BaseUtil.contains(url, '?', true)){
	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
	    	} else {
	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition;
	    	}
	    	myurl += "&datalistId=" + main.getActiveTab().id;
	    	main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页
	    	if(main._mobile) {
	    		main.addPanel(me.BaseUtil.getActiveTab().title+'('+title+')', myurl, panelId);
	    	} else {
	    		panel = {       
    	    			title : me.BaseUtil.getActiveTab().title+'('+title+')',
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
    	    			closable : true,
    	    			listeners : {
    	    				close : function(){
    	    					if(!main){
    	    						main = parent.parent.Ext.getCmp("content-panel");
    	    					}
    	    			    	main.setActiveTab(main.getActiveTab().id); 
    	    				}
    	    			} 
    	    	};
    	    	this.openTab(panel, panelId);
	    	}
    	}else{ 
	    	main.setActiveTab(panel); 
    	}
    },
    openQueryUrl: function(record) {
    	var me = this, arr = keyField.split('+'),ff = [],k = [];//vd_vsid@vd_id+vd_class@vd_class
		Ext.Array.each(arr, function(r){
			ff = r.split('@');
			k.push(ff[0] + '=' + record.get(ff[1]));
		});
		var myurl = k.join('&');
		var panelId = caller +  "_" + myurl;
    	var panel = Ext.getCmp(panelId); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!main){
			main = parent.parent.Ext.getCmp("content-panel");
		}
    	if(!panel){ 
    		var title = me.BaseUtil.getActiveTab().title + '-查询';
    		if(contains(url, '?', true)){
    			myurl = url + '&' + myurl;
    		} else {
    			myurl = url + '?' + myurl;
    		}
	    	if (main._mobile) {
	    		main.addPanel(title, myurl, panelId);
	    	} else {
	    		panel = {       
    	    			title : title,
    	    			tag : 'iframe',
    	    			tabConfig: {tooltip: title},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
    	    			closable : true,
    	    			listeners : {
    	    				close : function(){
    	    					if(!main){
    	    						main = parent.parent.Ext.getCmp("content-panel");
    	    					}
    	    			    	main.setActiveTab(main.getActiveTab().id); 
    	    				}
    	    			} 
    	    	};
    	    	this.openTab(panel, panelId);
	    	}
    	} else { 
	    	main.setActiveTab(panel); 
    	}
    },
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	/*var tab = main.getComponent(o); */
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    },
    getCurrentStore: function(value){
    	var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data[keyField];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data[keyField];
			}
			var v = item.data[keyField];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
    },
    showSearchListWin: function(){
 	   var me = this, win = this.searchWin;
 	   if (!win){
 		   win = this.searchWin = Ext.create('Ext.window.Window', {
 			   title: '高级查询',
 			   height: screen.height*0.7*0.8,
 			   width: screen.width*0.7*0.6,
 			   maximizable : true,
 			   closable: false,
 			   buttonAlign : 'center',
 			   layout : 'border',
 			   bodyStyle: 'background:#f1f1f1;',
 			   tools: [{
 				   type: 'close',
 				   handler: function(e, el, header, tool){
 					   tool.ownerCt.ownerCt.down('grid').setEffectData();//保留已选择的条件
 					   tool.ownerCt.ownerCt.hide();
 				   }
 			   }],
 			   items: [{
 				   xtype: 'form',
 				   region: 'north',
 				   layout: 'column',
 				   bodyStyle: 'background:#f1f1f1;',
 				   maxHeight: 100,
 				   buttonAlign: 'center',
 				   buttons: [{
 					   name: 'query',
 					   id: 'query',
 					   text: $I18N.common.button.erpQueryButton,
 					   iconCls: 'x-button-icon-query',
 					   cls: 'x-btn-gray',
 					   handler: function(btn){
 						   Ext.getCmp('grid').getCount(caller);
 						   btn.ownerCt.ownerCt.ownerCt.hide();
 					   }
 				   },{
 					   cls: 'x-btn-gray',
 					   text: '清空',
 					   handler: function(btn){
 						   btn.ownerCt.ownerCt.ownerCt.down('grid').store.loadData([{},{},{},{},{},{},{},{},{},{}]);
 						   Ext.getCmp('grid').getCount(caller);
 					   }
 				   },{
 					   cls: 'x-btn-gray',
 					   text: '关闭',
 					   handler: function(btn){
 						   btn.ownerCt.ownerCt.ownerCt.down('grid').setEffectData();
 						   btn.ownerCt.ownerCt.ownerCt.hide();
 					   }
 				   },{
 					   xtype: 'radio',
 					   name: 'separator',
 					   boxLabel: '与',
 					   checked: true,
 					   inputValue: 'AND',
 					   getCheckValue: function(){
 						   return this.checked ? 'AND' : 'OR';
 					   }
 				   },{
 					   xtype: 'radio',
 					   name: 'separator',
 					   boxLabel: '或',
 					   inputValue: 'OR'
 				   }]
 			   }, me.getSearchListGrid()]
 		   });
 		   Ext.getCmp('grid').searchGrid = win.down('grid');
 	   }
 	   win.show();
 	   win.down('grid').loadData();
    },
    getGridColumns : function() {
		   var grid = Ext.getCmp('grid'), columns = grid.headerCt.getGridColumns(), data = [];
		   Ext.each(columns, function(){
			   if(this.dataIndex && this.getWidth() > 0) {
				   data.push({
					   display : this.text,
					   value : this.text,
					   column : this
				   });
			   }
		   });
		   return data;
	   },
    getSearchListGrid: function(){
		   var data = this.getGridColumns();
		   var grid = Ext.create('Ext.grid.Panel', {
			   maxHeight: 350,
			   region: 'center',
			   store: Ext.create('Ext.data.Store', {
				   fields:[{
					   name: 'sl_label',
					   type: 'string'
				   },{
					   name: 'sl_field',
					   type: 'string'
				   },{
					   name: 'sl_type',
					   type: 'string'
				   },{
					   name: 'sl_dbfind',
					   type: 'string'
				   },{
					   name: 'union',
					   type: 'string'
				   },{
					   name: 'value'
				   }],
				   data: []
			   }),
			   columns: [{
				   text: '条件',
				   flex: 2,
				   dataIndex: 'sl_label',
				   editor: {
					   xtype: 'combo',
					   store : Ext.create('Ext.data.Store', {
						   fields : [ 'display', 'value', 'column' ],
						   data : data
					   }),
					   editable: false,
					   displayField : 'display',
					   valueField : 'value',
					   queryMode : 'local'
				   },
				   renderer : function(val, meta, record, x, y, store, view) {
					   if (val) {
						   var column = view.ownerCt.headerCt.getHeaderAtIndex(y);
						   if(column && typeof column.getEditor != 'undefined') {
							   var	editor = column.getEditor(record);
							   if (editor && editor.lastSelection.length > 0) {
								   var cm = editor.lastSelection[0].get('column'),
								   field = cm.dataIndex;
								   if (record.get('sl_field') != field)
									   record.set('sl_field', field);
								   var t = 'S';
								   if(cm.xtype == 'datecolumn' || cm.xtype == 'datetimecolumn') {
									   t = 'D';
								   } else if(cm.xtype == 'numbercolumn') {
									   t = 'N';
								   }
								   if (record.get('sl_type') != t)
									   record.set('sl_type', t);
							   }
						   }
					   } else {
						   if (record.get('sl_field')) {
							   record.set('sl_field', null);
						   }
					   }
					   return val;
				   },
				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
					   if (type == 'click' || type == 'dbclick') {
						   return true;
					   }
					   return false;
				   }
			   },{
				   text: '',
				   hidden: true,
				   dataIndex: 'sl_field',
				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
					   return false;
				   }
			   },{
				   text: '',
				   hidden: true,
				   dataIndex: 'sl_type',
				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
					   return false;
				   }
			   },{
				   text: '关系',
				   flex: 1,
				   dataIndex: 'union',
				   xtype:'combocolumn',
				   editor: {
					   xtype: 'combo',
					   store: Ext.create('Ext.data.Store', {
						   fields: ['display', 'value'],
						   data : [{"display": '等于', "value": '='},
						           {"display": '大于', "value": '>'},
						           {"display": '大于等于', "value": '>='},
						           {"display": '小于', "value": '<'},
						           {"display": '小于等于', "value": '<='},
						           {"display": '不等于', "value": '<>'},
						           {"display": '介于', "value": 'Between And'},
						           {"display": '包含', "value": 'like'},
						           {"display": '不包含', "value": 'not like'},
						           {"display": '开头是', "value": 'begin like'},
						           {"display": '开头不是', "value": 'begin not like'},
						           {"display": '结尾是', "value": 'end like'},
						           {"display": '结尾不是', "value": 'end not like'}]
					   }),
					   displayField: 'display',
					   valueField: 'value',
					   queryMode: 'local',
					   editable: false,
					   value: 'like'
				   },
				  /* renderer : function(v) {
					   var r = v;
					   switch(v) {
					   case 'like':
						   r = 'Like';break;
					   case '=':
						   r = '等于';break;
					   case '>':
						   r = '大于';break;
					   case '>=':
						   r = '大于等于';break;
					   case '<':
						   r = '小于';break;
					   case '<=':
						   r = '小于等于';break;
					   case '<>':
						   r = '不等于';break;
					   case 'Between And':
						   r = '介于';break;
					   }
					   return r;
				   },*/
				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
					   if (type == 'click' || type == 'dbclick') {
						   return true;
					   }
					   return false;
				   }
			   },{
				   text: '值',
				   flex: 3,
				   dataIndex: 'value',
				   renderer: function(val){
					   if(Ext.isDate(val)){
						   return Ext.Date.format(val, 'Y-m-d');
					   }
					   return val;
				   },
				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
					   if (type == 'click' || type == 'dbclick') {
						   var s = view.ownerCt.selModel, m = s.getSelection(), n = [];
						   Ext.Array.each(m, function(){
							   n.push(this);
						   });
						   n.push(view.ownerCt.store.getAt(recordIndex));
						   s.select(n);
						   return true;
					   }
					   return false;
				   }
			   }],
			   columnLines: true,
			   plugins: Ext.create('Ext.grid.plugin.CellEditing', {
				   clicksToEdit: 1,
				   listeners: {
					   beforeedit: function(e){
						   if(e.field == 'value'){
							   var record = e.record;
							   var column = e.column;
							   if(record.data['union'] == null || record.data['union'] == ''){
								   record.set('union', 'like');
							   }
							   var f = record.data['sl_field'];
							   switch(record.data['sl_type']){
							   case 'D':
								   switch(record.data['union']){
								   case 'Between And':
									   column.setEditor(new erp.view.core.form.FtDateField({
										   id: f,
										   name: f
									   }));break;
								   default:
									   column.setEditor(new Ext.form.field.Date({
										   id: f,
										   name: f
									   }));break;
								   }
								   break;
							   case 'S':
								   switch(record.data['union']){
								   case 'Between And':
									   column.setEditor(new erp.view.core.form.FtField({
										   id: f,
										   name: f,
										   value: e.value
									   }));break;
								   default:
									   column.setEditor(new Ext.form.field.Text({
										   id: f,
										   name: f
									   }));break;
								   }
								   break;
							   case 'N':
								   switch(record.data['union']){
								   case 'Between And':
									   column.setEditor(new erp.view.core.form.FtNumberField({
										   id: f,
										   name: f
									   }));break;
								   default:
									   column.setEditor(new Ext.form.field.Number({
										   id: f,
										   name: f
									   }));break;
								   }
								   break;
							   case 'T':
								   column.dbfind = record.get('sl_dbfind');
								   switch(record.data['union']){
								   case 'Between And':
									   column.setEditor(new erp.view.core.form.FtFindField({
										   id: f,
										   name: f
									   }));break;
								   default:
									   column.setEditor(new erp.view.core.trigger.DbfindTrigger({
										   id: f,
										   name: f
									   }));break;
								   }
								   break;
							   default:
								   column.setEditor(null);
							   }
						   }
					   }
				   }
			   }),
			   selModel: Ext.create('Ext.selection.CheckboxModel',{

			   }),
			   setEffectData: function(){
				   var me = this;
				   var datas = new Array();
				   Ext.each(me.selModel.getSelection(), function(item){
					   var data = item.data;
					   if(!Ext.isEmpty(data.sl_label) && !Ext.isEmpty(data.union) && !Ext.isEmpty(data.value)){
						   datas.push(data);
					   }
				   });
				   me.effectdata = datas;
			   },
			   getEffectData: function(){
				   return this.effectdata || new Array();
			   },
			   loadData: function(){
				   if(!this.effectdata) {
					   this.store.add([{},{},{},{},{},{},{},{},{},{}]);
				   }
			   },
			   /**
			    * 将数据拼成Sql条件语句
			    */
			   getCondition: function(){
				   this.setEffectData();
				   var condition = '';
				   var separator = this.up('window').down('form').down('radio').getCheckValue();
				   Ext.each(this.effectdata, function(data){
					   if(data.union == 'Between And'){
						   var v1 = data.value.split('~')[0];
						   var v2 = data.value.split('~')[1];
						   if(data.sl_type == 'D'){
							   if(condition == ''){
								   condition = '(' + data.sl_field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
								   + v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
							   } else {
								   condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
								   + v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
							   }
						   } else if(data.sl_type == 'N'){
							   if(condition == ''){
								   condition = '(' + data.sl_field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
							   } else {
								   condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
							   }
						   } else{
							   if(condition == ''){
								   condition = '(' + data.sl_field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
							   } else {
								   condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
							   }
						   }
					   } else {
						   if(data.sl_type == 'D'){
							   var v = data.value, field = data.sl_field;
							   if(Ext.isDate(v)) {
								   v = Ext.Date.format(v, 'Y-m-d');
							   }
							   if(data.union == '<' || data.union == '<=' || data.union == '>' || data.union == '>='){
								   v = "to_date('" + v + "','yyyy-MM-dd')";
							   }else {
								   v = Ext.Date.format(data.value, 'Ymd');
								   field = "to_char(" + field + ",'yyyymmdd')";
							   }
							   if(condition == ''){
								   condition = '(' + field + data.union + v + ') ';
							   } else {
								   condition += ' ' + separator +' (' + field + data.union + v + ') ';
							   }
						   } else {
							   var v = data.value;
							   if(data.union == 'like' || data.union=='not like'){
								   v = " '%" + data.value + "%'";
							   }else if(data.union =='begin like' || data.union =='begin not like'){
								   v = " '" + data.value + "%'";
								   data.union=data.union.substring(5);
							   }else if(data.union =='end like' || data.union=='end not like'){
								   v = " '%" + data.value + "'";
								   data.union=data.union.substring(3);
							   }else {
								   v = " '" + data.value + "'";
							   }
							   if(condition == ''){
								   condition = '(' + data.sl_field + " " + data.union + v + ") ";
							   } else {
								   condition += ' ' + separator +' (' + data.sl_field + " " + data.union + v + ") ";
							   }
						   }
					   }
				   });
				   return condition;
			   }
		   });
		   return grid;
	   },
    getFilterCondition: function(){
    	var fields = Ext.getCmp('grid').plugins[0].fields;
    	var items = new Array();
    	Ext.each(Ext.Object.getKeys(fields), function(key){
    		var item = fields[key];
    		if(item.value != null && item.value.toString().trim() != ''){
    			items.push({
    				xtype: item.xtype,
    				id: item.itemId,
    				fieldLabel: item.fieldLabel,
    				fieldStyle: item.fieldStyle,
    				value: item.value,
    				columnWidth: 0.5,
    				cls: 'form-field-border',
    				listeners: {
    					change: function(f){
    						Ext.getCmp(item.id).setValue(f.value);
    					}
    				}
    			});
    		}
    	});
    	return items;
    }
});