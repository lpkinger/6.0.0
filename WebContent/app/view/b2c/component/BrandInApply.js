Ext.define('erp.view.b2c.component.BrandInApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		disname=null;
		sw=null;
		searchword=null;
		formCondition = getUrlParam('formCondition');
		br_name = getUrlParam('br_name');
		br_engname = getUrlParam('br_engname');
		getbyUUid=false;
		Ext.apply(me, { 
				items: [{
					xtype: 'form',
					id:'form',
					autoScroll:true,
					anchor: '100% 100%',
					codeField:'br_code',
					keyField:'br_id',
					statusField:'br_status',
					statuscodeField:'br_statuscode',
					getIdUrl: 'common/getId.action?seq=BRANDINAPPLY_SEQ',
					deleteUrl:'b2c/product/deleteBrandInApply.action',
					submitUrl: 'b2c/product/submitBrandInApply.action',
					resSubmitUrl: 'b2c/product/resSubmitBrandInApply.action',
					auditUrl: 'b2c/product/auditBrandInApply.action',
					resAuditUrl: 'b2c/product/resAuditBrandInApply.action',
					tablename:'BrandInApply',
					title:'品牌入库申请',
					bodyPadding:'5 0 0 30',
					bodyStyle: 'background: #f1f1f1;',
					defaults:{
						xtype:'textfield',
					    labelStyle:"color:red;"
					},
					items:[{
						   xtype:"container",
						   layout:'column',
						   id:'basicinf',
						   hidden:true,
						   html:'<div  class="x-form-group-label" id="group1" style="background-color: #bfbfbe;height:22px!important;margin-bottom:10px" title="收拢"><h6>单据基本信息</h6></div>',
						   style:"padding-bottom: 4px;",
						   defaults:{
								xtype:'textfield',
							    labelStyle:"color:red;"
						   },
						   items:[{
					              id:'br_code',
					              name:'br_code',
					              xtype:'textfield',
					              columnWidth:.3,
					              fieldLabel:'单据编号',
					              labelStyle:"color:black;",
					              readOnly: true,
					              fieldStyle:'background-color: #e8e8e8;'
						   },{
					        	  id:'br_recorder',
					              name:'br_recorder',
					              columnWidth:.3,
					              fieldLabel:'录入人',
					              labelStyle:"color:black;",
					              readOnly: true,
					              fieldStyle:'background-color: #e8e8e8;'
						   },{
								  id:'br_indate',
								  name:'br_indate',
								  xytpe:'datefield',
								  value:new Date(),
								  columnWidth:.3,
								  fieldLabel:'录入时间',
					              labelStyle:"color:black;",
					              readOnly: true,
					              fieldStyle:'background-color: #e8e8e8;'
						   },{
					        	  id:'br_status',
					              name:'br_status',
					              xtype:'textfield',
					              columnWidth:.3,
					              fieldLabel:'录入状态',
					              value:'在录入',
					              labelStyle:"color:black;",
					              readOnly: true,
					              fieldStyle:'background-color: #e8e8e8;'					          
						   },{
					        	  id:'br_b2cstatus',
					              name:'br_b2cstatus',
					              columnWidth:.3,
					              fieldLabel:'商城审批状态',
					              logic:'ignore',
					              labelStyle:"color:black;",
					              readOnly: true,
					              fieldStyle:'background-color: #e8e8e8;'
						   },{
					        	  id:'br_b2cauditopinion',
					              name:'br_b2cauditopinion',
					              xtype:'textarea',
					              columnWidth:.6,
					              fieldLabel:'商城审批意见',
					              logic:'ignore',
					              labelStyle:"color:black;",
					              readOnly: true,
					              fieldStyle:'background-color: ##e8e8e8;'
						   }]
						  },{
					   xtype:"container",
					   html:'<div  class="x-form-group-label" id="group1" style="background-color: #bfbfbe;height:22px!important;" title="收拢"><h6>品牌基本信息</h6></div>',
					   style:"padding-bottom: 4px;"
					},{ 
						xtype:'dbfindtrigger',
					    name:'br_engname',
					    id:'br_engname',
					    fieldLabel:'英文品牌',
					    width:400,
					    autoDbfind:false,
					    allowBlank : false,
					    enableKeyEvents:true,
					    listeners:{
					    	keyup: function(field){	
					    		if(formCondition==''||formCondition==null){
					    			if(br_name==null||br_engname==''){
					    				me.getSerarchData(field,'br_engname','searchEn');
					    			}
					    		}
			            	}
					    },
					    onTriggerClick:function(){
					    	me.createWindow();
					    },
					    group:1,
					    groupName:"品牌基本信息"
					},{
						xtype:'dbfindtrigger',
					    name:'br_name',
					    id:'br_name',
					    fieldLabel:'中文品牌',
					    width:400,
					    allowBlank : false,
					    enableKeyEvents:true,
					    autoDbfind:false,
					    listeners:{
					    	keyup: function(field){
					    		if(formCondition==''||formCondition==null){
					    			if(br_name==null||br_engname==''){
					    				me.getSerarchData(field,'br_name','searchEn');
					    			}
					    		}
			            	},
					    },
					    onTriggerClick:function(){
					    	me.createWindow();
					    },
					    group:1,
					    groupName:"品牌基本信息"
					},{
						name:'br_vendor',
						id:'br_vendor',
						fieldLabel:'原厂名称',
						width:1000,
						allowBlank : false,
						group:1,
					    groupName:"品牌基本信息"
				    },{
						name:'br_url',
						id:'br_url',
						fieldLabel:'官网地址',
						width:1000,
						allowBlank : false,
						group:1,
					    groupName:"品牌基本信息"
				    },{
				    	name:'br_logourl',
				    	id:'br_logourl',
				    	hidden:true,
				    	allowBlank:false				    	
				    },{
			              id:'fileform',
			              xtype:'form',
			              bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
			              items:[{
			                  id:'br_logo',
			                  xtype: 'filefield',
			                  labelStyle:"color:red;",
			                  name: 'file',
			                  fieldLabel:'Logo',
			                  buttonOnly:true,
			                  group:1,
			                  groupName:"品牌基本信息",
			                  createFileInput : function() {
				                   var a = this;
				                    a.fileInputEl = a.button.el.createChild({
							           name : a.getName(),
							           cls : Ext.baseCSSPrefix + "form-file-input",
							           tag : "input",
							           type : "file",
							           size : 1,
							           accept:"image/*"
						           }).on("change", a.onFileChange, a)
			                   },
			                  buttonConfig:{
			                      text:'选择图片',
			                      iconCls:'x-button-icon-pic'
			                  },
			                  listeners: {
			                      change: function(field){
			                          field.ownerCt.upload(field);
			                      }
			                  }
			              },{
			                  xtype: 'displayfield',			                 
			                  value: '建议尺寸150*90像素左右',
			                  group:1,
			                  groupName:"品牌基本信息",
			                  margin:'5 0 10 110'		                 
			              },{
			                  xtype: 'image',
			                  width: 155,
			                  height: 96,
			                  id:'logo',
			                  hidden:true,
			                  margin:'5 0 10 110'
			              }],
			               upload: function(field){
			               	 var img_reg= /\.([jJ][pP][gG]){1}$|\.([jJ][pP][eE][gG]){1}$|\.([gG][iI][fF]){1}$|\.([pP][nN][gG]){1}$|\.([bB][mM][pP]){1}$/;
							 if (img_reg.test(field.value)) {
		                          field.ownerCt.getForm().submit({
		                              url: basePath + 'b2c/product/saveBrandLogo.action',
		                              waitMsg:'正在上传',
		                              success: function(fp, o){
		                                  if(o.result.success){
		                                      Ext.Msg.alert('提示','上传成功');  
		                                      var logo=Ext.getCmp('logo');
		                                      logo.show();
		                                      logo.setSrc(o.result.filepath);
		                                      Ext.getCmp('br_logourl').setValue(o.result.filepath);
		                                  }else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
		                              },
		                              failure:function(fp,o){
		                                  Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
		                              }
		                          });  
							 }else{
							 	 Ext.Msg.alert('提示', '文件类型错误,请选择图片文件(jpg/jpeg/gif/png/bmp)');
							 }
			              }
			            },{
				       xtype:'htmleditor',
				       fieldLabel:'简介',
				       id:'br_brief',
				       name:'br_brief',
				       width:1000,
				       allowBlank: false,
				       group:1,
					   groupName:"品牌基本信息"
				    },{
				    	xtype: 'checkboxgroup',
			            fieldLabel : '销售区域',
			            id:'br_area',
			            group:1,
					    groupName:"品牌基本信息",
			            defaults: {
			                flex: 1
			            },
			            layout: 'hbox',
			            items: [
			                {
			                    boxLabel  : '大陆',
			                    name      : 'br_area',
			                    inputValue: '大陆',
			                    id        : 'checkbox10'
			                }, {
			                    boxLabel  : '港澳台',
			                    name      : 'br_area',
			                    inputValue: '港澳台',
			                    id        : 'checkbox11'
			                }, {
			                    boxLabel  : '日韩',
			                    name      : 'br_area',
			                    inputValue: '日韩',
			                    id        : 'checkbox12'
			                }, {
			                    boxLabel  : '欧美',
			                    name      : 'br_area',
			                    inputValue: '欧美',
			                    id        : 'checkbox13'
			                }
			            ]
				    },{
				    	xtype: 'fieldcontainer',
                        margin:'5 0 10 110',
				    	padding:'5 5 5 5',
                        combineErrors: true,
                        group:2,
					    groupName:"品牌应用",
                        msgTarget: 'under',
                        defaults: {
                            hideLabel: true
                        },
                        items: [{
                        	layout:'column',
                        	bodyStyle: 'background: #f1f1f1;border:none',
				    		items:[{
		                        xtype:'button',
		                        text: '其他区域',
			    				cls: 'x-dd-drop-ok-add',
			    				iconCls: 'x-dd-drop-icon',
		                        columnWidth:0.1,
								handler:function(btn){
									var f = btn.ownerCt;
				    				f.insert(f.items.length, {
				    					xtype:'triggerfield',
					    				name: 'otherArea',
					    				columnWidth:.40,
					    				style:'margin-left:5px;',
		                                triggerCls : Ext.baseCSSPrefix + "form-clear-trigger",
									    onTriggerClick:function(){
									       this.ownerCt.remove(this);
									    }
				    				});
								}
							 },{
							   xtype: 'displayfield',
							   columnWidth:0.9,
							   value: '如果您的区域不在上述范围，您可以手动添加其他区域'
						   }]
				       }]					 
				    },{
				    	 xtype:"container",
						 html:'<div  class="x-form-group-label" id="group2" style="z-index:0;background-color: #bfbfbe;height:22px!important;" title="收拢"><h6>品牌应用</h6></div>',
						 style:"padding-bottom: 4px;"
				    },{					    					    	
					    name:'br_series',
					    id:'br_series',
					    fieldLabel:'主打产品',
					    xtype:'textareafield',
					    grow : true,
					    width:1000,
					    heigth:100,
					    allowBlank: false,
					    group:2,
					    groupName:"品牌应用"
					},{
			            xtype: 'checkboxgroup',
			            fieldLabel: '应用领域',
			            group:2,
					    groupName:"品牌应用",
					    id:'br_application',
			            defaults: {
			                flex: 1
			            },
			            layout: 'hbox',
			            items: [
			                {
			                    boxLabel  : '移动手持',
			                    name      : 'br_application',
			                    inputValue: '移动手持',
			                    id        : 'checkbox1'
			                }, {
			                    boxLabel  : '医疗电子',
			                    name      : 'br_application',
			                    inputValue: '医疗电子',
			                    id        : 'checkbox2'
			                }, {
			                    boxLabel  : '消费电子',
			                    name      : 'br_application',
			                    inputValue: '消费电子',
			                    id        : 'checkbox3'
			                }, {
			                    boxLabel  : '通信网络',
			                    name      : 'br_application',
			                    inputValue: '通信网络',
			                    id        : 'checkbox4'
			                }, {
			                    boxLabel  : '汽车电子',
			                    name      : 'br_application',
			                    inputValue: '汽车电子',
			                    id        : 'checkbox5'
			                }, {
			                    boxLabel  : '能源控制',
			                    name      : 'br_application',
			                    inputValue: '能源控制',
			                    id        : 'checkbox6'
			                }, {
			                    boxLabel  : '家用电器',
			                    name      : 'br_application',
			                    inputValue: '家用电器',
			                    id        : 'checkbox7'
			                }, {
			                    boxLabel  : '工业控制',
			                    name      : 'br_application',
			                    inputValue: '工业控制',
			                    id        : 'checkbox8'
			                }, {
			                    boxLabel  : '安防监控',
			                    name      : 'br_application',
			                    inputValue: '安防监控',
			                    id        : 'checkbox9'
			                }
			            ]
				    },{
				    	xtype: 'fieldcontainer',
                        margin:'5 0 10 110',
				    	padding:'5 5 5 5',
                        combineErrors: true,
                        group:2,
					    groupName:"品牌应用",
                        msgTarget: 'under',
                        defaults: {
                            hideLabel: true
                        },
                        items: [{
                        	layout:'column',
                        	bodyStyle: 'background: #f1f1f1;border:none',
				    		items:[{
		                        xtype:'button',
		                        text: '其他领域',
			    				cls: 'x-dd-drop-ok-add',
			    				iconCls: 'x-dd-drop-icon',
		                        columnWidth:0.1,
								handler:function(btn){
									var f = btn.ownerCt;
				    				f.insert(f.items.length, {
				    					xtype:'triggerfield',
					    				name: 'otherApplication',
					    				columnWidth:.40,
					    				style:'margin-left:5px;',
		                                triggerCls : Ext.baseCSSPrefix + "form-clear-trigger",
									    onTriggerClick:function(){
									       this.ownerCt.remove(this);
									    }
				    				});
								}
							 },{
							   xtype: 'displayfield',
							   columnWidth:0.9,
							   value: '如果您的品牌应用领域不在上述范围，您可以手动添加其他领域'
						   }]
				       }]					 
				    },{
				    	xtype:'htmleditor',
				    	name:'br_achievement',
				    	id:'br_achievement',
				    	fieldLabel:'主要成就',
				    	width:1000,
				    	allowBlank: false,
				    	labelStyle:"color:black;",
				    	group:2,
					    groupName:"品牌应用"
				    },{
				    	id:'br_version',
				    	name:'br_version',
				    	hidden:true
				    },{
					    id:'br_id',
					 	name:'br_id',
					 	hidden:true
					},{
					    id:'br_uuid',
					 	name:'br_uuid',
					 	hidden:true,
					 	listeners:{
					 		change:function(){
					 			if(getbyUUid!=false){
					 				Ext.Ajax.request({
		    		   		        	url : basePath + 'b2c/product/getBrandDataByUUID.action',
		    		   		        	params: {
		    		   		        		UUID: Ext.getCmp('br_uuid').value
		    		   		        	},
		    		   		        	method : 'post',
		    		   		        	callback : function(options,success,response){    
		    		   		        		var res = new Ext.decode(response.responseText);
		    		   		        		if(res.exceptionInfo != null){
		    		   		        			showError(res.exceptionInfo);return;
		    		   		        		}else{
		    		   		        		    me.setFormValues(res.data);
		    		   		        		}
		    		   		        	}
					 				});
					 			}
						 	}
					 	}
					},{	
					 	id:'br_date',
						name:'br_date',
						xytpe:'datefield',
						defaultvalue:new Date(),
						hidden:true
					 },{
					 	id:'br_statuscode',
					 	name:'br_statuscode',
					 	value:'',
					 	hidden:true
					 },{
					 	id:'br_auditman',
					 	name:'br_auditman',
					 	hidden:true
					 },{
					 	id:'br_auditdate',
					 	name:'br_auditdate',
					 	hidden:true
					 }]	,	
				buttonAlign:'center',
				buttons:[{
					xtype:'erpSaveButton',
					hidden:_nobutton==1 || !Ext.isEmpty(formCondition)
				},{
					xtype:'erpUpdateButton',
					hidden:_nobutton==1 || Ext.isEmpty(formCondition)
				},{
					xtype:'erpSubmitButton',
					hidden:_nobutton==1 || Ext.isEmpty(formCondition)
				},{
					xtype:'erpDeleteButton',
					hidden:_nobutton==1 || Ext.isEmpty(formCondition)
				},{
					xtype:'erpResSubmitButton',
					hidden:_nobutton==1 || Ext.isEmpty(formCondition)
				},{
		 			xtype:'erpAuditButton',
		 			hidden:_nobutton==1 || Ext.isEmpty(formCondition)
				},{
		 			text:'修 改',
		 			id:'change',
		 			name:'change',
		 			hidden:_nobutton==1 || Ext.isEmpty(formCondition)
				}]
		   }]
	  }); 
	  me.getData();
	  me.callParent(arguments); 
	},
	getData:function(){
		var me = this;
		//从url解析参数
		if(formCondition != null && formCondition != ''){
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
			this.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'b2c/product/getBrandData.action',
	        	params: {
	        		caller: caller, 
	        		id: formCondition.split("=")[1],
	        		_noc: (getUrlParam('_noc') || me._noc)
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		getbyUUid=false;
	        		me.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else{
	        			me.setFormValues(res.data);
	        		}
	        	}
	        });
		}else if(br_name!=null&&br_name!=''&&br_engname!=null&&br_engname!=''){
			Ext.Ajax.request({
		        	url : basePath + 'b2c/product/getUpdateBrand.action',
		        	params: {
		        		nameCn: br_name,
		        		nameEn: br_engname
		        	},
		        	method : 'post',
		        	callback : function(options,success,response){
   		        		var res = new Ext.decode(response.responseText);
   		        		if(res.exceptionInfo != null){
   		        			showError(res.exceptionInfo);return;
   		        		}else{
   		        			me.setFormValues(res.data);
   		        		}
		        	}
		        });
			}
	},	
	getSerarchData:function(field,name){
		var me = this;
		//全局变量用来记录填充的文本框
		disname=name;
	    searchword=Ext.getCmp(disname).value;
		if(disname=='br_name'){
			displayname='nameCn';
		}else{
			displayname='nameEn';
		}
		bl=Ext.getCmp('searchMenu');
	 
		if(!bl){
			   bl=Ext.create('Ext.menu.Menu', {
		        	id:'searchMenu',
		            bodyBorder:false,
		            showSeparator:false,
		            width:field.bodyEl.dom.clientWidth,
		            renderTo: Ext.getBody(),
		            items: [Ext.widget('boundlist',{
		                name:'searchEn',
		                displayField:displayname,
		                renderTo:Ext.getBody(),
		                loading:true,
		                store:Ext.create('Ext.data.Store', {
		                    fields: [ {name: 'nameCn'},
		                              {name:'id'},
		                              {name:'uuid'},
		                              {name:'nameEn'}],
		                              proxy: {
		                                  type: 'ajax',
		                                  async: false,
		                                  url: basePath+"b2c/product/getSearchData.action?caller='DeviceInApply2'",
		                                  extraParams :{
		                                      searchWord:searchword
		                                  },
		                                  reader: {
		                                      idProperty:'nameEn',
		                                      type: 'json',
		                                      root: 'data.data'
		                                  } 
		                              },
		                       autoLoad:false  
		                }),
		                listeners:{
		    		   		itemclick:function(field,record){
		    		   				getbyUUid=true;
		    		   				Ext.getCmp('br_name').setValue(record.data.nameCn);
		    		   				Ext.getCmp('br_engname').setValue(record.data.nameEn);
		    		   				Ext.getCmp('br_uuid').setValue(record.data.uuid);
		    		   			    bl.hide();
		    		   		}
		    		   	}
		            })]
			   });      
		}
		bl.down('boundlist').getStore().load({params :{
            	searchWord:searchword
        }});
		bl.mon(Ext.getDoc(),'mousedown',me.collapseIf,bl);
		bl.alignTo(field.bodyEl,'tl-bl?');
		bl.show();	
		Ext.getCmp(disname).focus(false,true);
    },
    
    setFormValues:function(data){//给Form 赋值
		   var form = Ext.getCmp('form');
		   var o = {};
		   o.br_application = data.br_application.split(",");
		   data.br_application = o;
		   o.br_area = data.br_area.split(",");
		   data.br_area = o;
		   
		 //如果领域和区域是其他，form 增加checkboxgroup 的值
		  var a = Ext.getCmp('br_application'),ar = Ext.getCmp('br_area');
		  var  application = [],area = [];
		  a.eachBox(function(cb) {
			  application.push(cb.inputValue);
		  });
		  ar.eachBox(function(cb) {
			  area.push(cb.inputValue);
		  });
		  var amin = Ext.Array.difference(o.br_application,application),armin = Ext.Array.difference(o.br_area,area);
		  Ext.Array.each(amin, function(value) {
		        a.insert(a.items.items.length,{
		        	xtype     : 'checkbox',
                    boxLabel  : value,
                    name      : 'br_application',
                    inputValue: value,
                    id        : 'checkbox'+value
		        });
		   });
		  Ext.Array.each(armin, function(value) {
		        ar.insert(ar.items.items.length,{
		        	xtype     : 'checkbox',
                    boxLabel  : value,
                    name      : 'br_area',
                    inputValue: value,
                    id        : 'checkbox'+value
		        });
		   });
		   if(data.br_logourl){
			   	 var logo=Ext.getCmp('logo');
				 logo.show();
				 logo.setSrc(data.br_logourl);
		   }
		   form.getForm().setValues(data);					  
		   if(data.br_statuscode = 'AUDITED'){
		   	   form.readOnly = true;
		   }
    },
    collapseIf: function(e){
        var me = this;
        if (!me.hidden && !e.within(me.inputEl, false, true) && !e.within(me.el, false, true)) {
            me.hide();
        }
    },
    createWindow:function(){
        var store =null;
        Ext.define('Brand', {
            extend: 'Ext.data.Model',
            fields: [
               {name: 'id'},
               {name: 'nameCn'},
               {name: 'nameEn'},
               {name: 'venodr'},
               {name: 'uuid'},
               {name: 'url'}
            ]        
        });
        Ext.Ajax.request({
            url : basePath +"b2c/product/getAllBrand.action?caller=DeviceInApply",
            method : 'post',
            async:false,
            callback : function(opt, s, res){
                var r = new Ext.decode(res.responseText);
                store = Ext.create('Ext.data.Store', {
                    model: 'Brand',
                    pageSize: 15,
                    proxy: {
                        type: 'pagingmemory',
                        data: r.data.data,
                        reader: {
                            type: 'json'
                        }
                    }
                }); 
            }
        });
        var win=Ext.create('Ext.window.Window',{
            width: '50%',
            height:565,
            id:'brandWindow',
            title:'选择品牌',
            layout: 'vbox',
            items:[{
                xtype:'grid',
                width:'100%',
                id:'brandGrid',
                store:store,
                columnLines:true,
                plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
                columns: [{
                    id:'brandid',
                    dataIndex: 'id',
                    flex: 1,
                    hidden:true
                },{
                    id:'branduuid',
                    dataIndex:'uuid',
                    flex: 1,
                    hidden:true
                },{
                     id:'brandname',
                     text: '中文品牌',
                     sortable: true,
                     dataIndex: 'nameCn',
                     filter:{ xtype:'textfield'},
                     flex: 1
                 },{
                     id:'brandnameeng',
                     text: '英文品牌',
                     sortable: true,
                     dataIndex: 'nameEn',
                     filter:{ xtype:'textfield'},
                     flex: 1
                 },{
                     id:'venodr',
                     text: '供应商',
                     sortable: true,
                     dataIndex: 'venodr',
                     filter:{ xtype:'textfield'},
                     flex: 1
                 }]        
            }],
             dockedItems:[{
                 dock:'bottom',
                 bbar: [ '->',{
                    text: '关  闭',
                    iconCls: 'x-button-icon-close',
                    cls: 'x-btn-gray',
                    handler: function() {
                        Ext.getCmp('brandWindow').close();
                    }
                },{
                    text: '重置条件',
                    id: 'reset',
                    hidden:true,
                    dock: 'bottom', 
                    cls: 'x-btn-gray',
                    handler: function() {
                    var grid = Ext.getCmp('brandWindow').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
                    grid.resetCondition();
                    grid.getCount();
                }}, '->']
            },{
                 xtype:'pagingtoolbar',
                 id:'paging',
                 store: store,
                 dock: 'bottom', 
                 displayInf:true
             }]
        });
        store.load();
        win.show(); 
    }
});