Ext.define('erp.view.b2c.component.DeviceInApply',{ 
  extend: 'Ext.Viewport', 
  layout: 'fit', 
  hideBorders: true, 
  initComponent : function(){ 
    var me = this; 
    //器件类目参数的数量
    propertiesnum=0;
    //渲染的A类型的组件的ID
    widgetID=null;
    copy_pic_att=getUrlParam('pic_att');
    de_id = getUrlParam('de_id');
    updateBeforeSubmit=false;
    getBrand=false;
    getKind=false;
    formCondition = getUrlParam('formCondition');
    Ext.apply(me, { 
        items: [{
          xtype: 'form',
          id:'form',
          keyField:'de_id',
          codeField:'de_code',
          statusField:'de_status',
          statuscodeField:'de_statuscode',
          getIdUrl: 'common/getId.action?seq=DEVICEINAPPLY_SEQ',
          deleteUrl:'/b2c/product/deleteDeviceInApply.action',
          submitUrl: '/b2c/product/submitDeviceInApply.action?caller='+caller+'&_noc=1',
          resSubmitUrl: '/b2c/product/resSubmitDeviceInApply.action?caller='+caller+'&_noc=1',
          auditUrl: '/b2c/product/auditDeviceInApply.action',
          resAuditUrl: '/b2c/product/resAuditDeviceInApply.action',
          tablename:'DeviceInApply',
          margin:'10 0 0 0',
          autoScroll:true,
          bodyStyle: 'background: #E8E8E8;',   
          layout:'column',
          defaults:{
            columnWidth:.33,
            xtype:'textfield',
            labelStyle:"color:red;",
            margin:'5 5 5 5'
          },
          items:[{
              allowBlank: false,
              allowDecimals: true,
              border: false,
              columnWidth:1,  
              html: "<div class='x-form-group-label' id='group0' style='background-color: #E8E8E8;height:22px;width:80%;!important;' title='收拢'><h6>单据基本信息</h6></div>",
              xtype: "container",
          },{
              id:'de_code',
              name:'de_code',
              columnWidth:.3,
              fieldLabel:'单据编号',
              labelStyle:"color:black;",
              readOnly: true,
              hidden:true,
              fieldStyle:'background-color: #e8e8e8;'
          },{
        	  xtype:'dbfindtrigger',
              id:'de_prodcode',
              name:'de_prodcode',
              columnWidth:.3,
              fieldLabel:'物料编号',
              editable:false,
              allowBlank:false,
              fieldStyle:'background-color: #e8e8e8;'
          },{
              id:'de_recorder',
              name:'de_recorder',
              columnWidth:.3,
              fieldLabel:'录入人',
              hidden:true,
              logic:'ignore',
              labelStyle:"color:black;",
              readOnly: true,
              fieldStyle:'background-color: #e8e8e8;'
          },{
              id:'de_indate',
              name:'de_indate',
              columnWidth:.3,
              fieldLabel:'录入时间',
              hidden:true,
              logic:'ignore',
              labelStyle:"color:black;",
              readOnly: true,
              fieldStyle:'background-color: #e8e8e8;'
          },{ 
             id:'de_status',
             name:'de_status',
             columnWidth:.3,
             fieldLabel:'录入状态',
             hidden:true,
             logic:'ignore',
             labelStyle:"color:black;",
             value:'在录入',
             readOnly: true,
             fieldStyle:'background-color: #e8e8e8;'
          },{
        	  id:'de_b2cstatus',
              name:'de_b2cstatus',
              columnWidth:.3,
              hidden:true,
              fieldLabel:'商城审批状态',
              logic:'ignore',
              labelStyle:"color:black;",
              readOnly: true,
              fieldStyle:'background-color: #e8e8e8;'
          },{
        	  id:'de_b2cauditopinion',
              name:'de_b2cauditopinion',
              xtype:'textarea',
              columnWidth:.6,
              fieldLabel:'商城审批意见',
              hidden:true,
              logic:'ignore',
              labelStyle:"color:black;",
              readOnly: true,
              fieldStyle:'background-color: ##e8e8e8;'
          },{
              id:'de_uuid',
              name:'de_uuid',
              hidden:true
          },{
              id:'de_version',
              name:'de_version', 
              hidden:true
          },{
             id:'de_statuscode',
             name:'de_statuscode',
             value:'',
             hidden:true
          },{
            id:'de_id',
            name:'de_id',
            xtype:'textfield',
            hidden:true
          },{
              allowBlank: false,
              allowDecimals: true,
              border: false,
              group: 0,
              columnWidth:1,  
              html: "<div class='x-form-group-label' id='group3' style='background-color: #E8E8E8;height:22px;width:80%;!important;' title='收拢'><h6>商品基本信息</h6></div>",
              xtype: "container",
          },{
        	xtype:'combo',
        	fieldLabel:'品牌',
            queryMode: 'local',
            id:'de_brand',
            name:'de_brand',
            displayField: 'nameCn',
            valueField: 'nameCn',
            hasLoad:false,
            columnWidth:.3,
            allowBlank:false,
            enableKeyEvents:true,
            triggerCls: 'x-form-search-trigger',
            setValue: function(value, doSelect) {
                var me = this,
                    valueNotFoundText = me.valueNotFoundText,
                    inputEl = me.inputEl,
                    i, len, record,
                    models = [],
                    displayTplData = [],
                    processedValue = [];

                if (me.store.loading) {
                    me.value = value;
                    me.setHiddenValue(me.value);
                    return me;
                }

                value = Ext.Array.from(value);
               
                for (i = 0, len = value.length; i < len; i++) {
                    record = value[i];
                    if (!record || !record.isModel) {
                        record = me.findRecordByValue(record);
                    }
                    if (record) {
                        models.push(record);
                        displayTplData.push(record.data);
                        processedValue.push(record.get(me.valueField));
                    }
                    else {
                        if (!me.forceSelection) {
                            displayTplData.push(value[i]);
                            processedValue.push(value[i]);
                        }
                        else if (Ext.isDefined(valueNotFoundText)) {
                            displayTplData.push(valueNotFoundText);
                        }
                    }
                }

                me.setHiddenValue(processedValue);
                me.value = me.multiSelect ? processedValue : processedValue[0];
                if (!Ext.isDefined(me.value)) {
                    me.value = null;
                }
                me.displayTplData = displayTplData; 
                me.lastSelection = me.valueModels = models;

                if (inputEl && me.emptyText && !Ext.isEmpty(value)) {
                    inputEl.removeCls(me.emptyCls);
                }
                me.setRawValue(me.getDisplayValue());
                me.checkChange();
                //如果没有获取过参数
                if(getBrand){
                	  if(formCondition==null&&de_id==null){
                      	Ext.getCmp('de_branduuid').setValue(record.get('uuid'));
                      	Ext.getCmp('de_brandid').setValue(record.get('id'));
                      }   
                }   
                if (doSelect !== false) {                	
                    me.syncSelection();
                }
                me.applyEmptyText();
              
                return me;
            },
            store:Ext.create('Ext.data.Store', {
                fields: [ { name:'nameCn'},
                          { name:'nameEn'},
                          { name:'id'  },
                          { name:'uuid'}],
                          proxy: {
                              type: 'ajax',
                              async: false,
                              url: basePath+"b2c/product/getAllBrand.action?caller=DeviceInApply",
                              reader: {
                                  idProperty:'nameEn',
                                  type: 'json',
                                  root: 'data.data'
                              },
                          },
                   autoLoad:false   
            }),
            listeners: {
                keydown:function(field){
                	me.getComboData(field);
                }
            },
            onTriggerClick:function(){
                var store =null;
                Ext.define('Brand', {
                    extend: 'Ext.data.Model',
                    idProperty: 'brand',
                    fields: [
                       {name: 'id'},
                       {name: 'nameCn'},
                       {name: 'nameEn'},
                       {name: 'venodr'},
                       {name: 'uuid'}
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
                            autoLoad:false,
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
                    layout: 'anchor',
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
                            filter:{ 
                           	 xtype:'textfield',
                            },
                            hidden:true
                        },{
                            id:'branduuid',
                            dataIndex:'uuid',
                            flex: 1,
                            filter:{ 
                           	 xtype:'textfield',
                            },
                            hidden:true
                        },{
                             id:'brandname',
                             text: '中文品牌',
                             sortable: true,
                             dataIndex: 'nameCn',
                             filter:{ 
                            	 xtype:'textfield',
                             },
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
                        }, '->']
                    },{
                         xtype:'pagingtoolbar',
                         id:'paging',
                         store: store,
                         dock: 'bottom', 
                         displayInf:true,
                     }]
                });
                store.load();
                win.show(); 
            }
          },{
              xtype:'combo',
              trigger1Cls: Ext.baseCSSPrefix + 'form-clear-trigger',
              trigger2Cls:'x-form-search-trigger',
              name:'de_kind',
              id:'de_kind',
              columnWidth:.3,
              fieldLabel:'所属类目',
              displayField:'nameCn',
              valueField:'nameCn',  
              allowBlank:false,
              autoLoad:false,
              enableKeyEvents:true,          
              listeners:{
                  keyup: function(field){ 
                      me.getKindData(field);
                  }
              },
              setValue: function(value, doSelect) {
                  var me = this,
                  valueNotFoundText = me.valueNotFoundText,
                  inputEl = me.inputEl,
                  i, len, record,
                  models = [],
                  displayTplData = [],
                  processedValue = [];

              if (me.store.loading) {
                  me.value = value;
                  me.setHiddenValue(me.value);
                  return me;
              }

              value = Ext.Array.from(value);
              
              for (i = 0, len = value.length; i < len; i++) {
                  record = value[i];
                  if (!record || !record.isModel) {
                      record = me.findRecordByValue(record);
                  }
                
                  if (record) {
                      models.push(record);
                      displayTplData.push(record.data);
                      processedValue.push(record.get(me.valueField));
                  }
                  else {
                      if (!me.forceSelection) {
                          displayTplData.push(value[i]);
                          processedValue.push(value[i]);
                      }
                      else if (Ext.isDefined(valueNotFoundText)) {
                          displayTplData.push(valueNotFoundText);
                      }
                  }
              }

              	me.setHiddenValue(processedValue);
              	me.value = me.multiSelect ? processedValue : processedValue[0];
              	if (!Ext.isDefined(me.value)) {
                  me.value = null;
              	}
              	me.displayTplData = displayTplData; 
              	me.lastSelection = me.valueModels = models;
              	
              	if (inputEl && me.emptyText && !Ext.isEmpty(value)) {
              		inputEl.removeCls(me.emptyCls);
              	}
              	if(me.getDisplayValue()!=''){
              		 me.setRawValue(me.getDisplayValue());
              	}
              	 me.checkChange();
              //如果没有获取过参数
              	if(getKind){
              	  if(formCondition==null&&de_id==null){
                    	Ext.getCmp('de_kind').setRawValue(record.get('nameCn'));
                    	Ext.getCmp('de_kindid').setValue(record.get('id'));
                    }   
              	}   
            
              	if (doSelect !== false) {                	
                  me.syncSelection();
              	}
              		me.applyEmptyText();
              		return me;
              },
              store:Ext.create('Ext.data.Store',{
                  fields: [ { name:'nameCn'},
                            { name:'id'  }],
                            proxy: {
                                type: 'ajax',
                                async: false,
                                url: basePath+"b2c/product/getKindData.action?caller=DeviceInApply",
                                reader: {
                                    idProperty:'id',
                                    type: 'json',
                                    root: 'data.data'
                                },
                            },
                     autoLoad:false   
              }),
              onTrigger1Click : function(){
                  var me = this,kindid=Ext.getCmp('de_kindid');
                  me.setValue(null);
                  kindid.setValue(null);
              },
              onTrigger2Click:function(){
                  me.onCellItemClick();
              }
          },{
              name:'de_oldspec',
              id:'de_oldspec',
              fieldLabel:'原厂型号',
              columnWidth:.3,
              allowBlank:false,
              enableKeyEvents:true,
              listeners: { 
                  keyup: function(field){ 
                      me.getOldSpecData(field,'de_oldspec');
                  }
              }
          },{
        	  xtype:'textfield',
        	  autoLoad:false,
              name:'de_packingspec',
              id:'de_packingspec',
              displayField:'packaging',
              fieldLabel:'封装',
              allowBlank:false,
              columnWidth:.3,
              defaultListConfig:{
                  loadMask: false
              }
//              store: Ext.create('Ext.data.Store', {
//            	    fields : ['packaging'],
//				    proxy: {
//			             type: 'ajax',
//					     url : basePath + 'b2c/product/getPackaging.action',				           
//					     reader: {
//					          type: 'json',
//					          root: 'data.packaging'
//					     },
//					     extraParams:{kindid:0},
//					     headers: {
//			                 'Content-Type': 'application/json;charset=utf-8'
//			             }		                   
//			           }
//             })
          },{
              xtype:'combo',
              name:'de_unit',
              id:'de_unit',
              allowBlank:false,
              fieldLabel:'单位',
              columnWidth:.3,
              displayField: 'unit',
              store:Ext.create('Ext.data.Store', {
                    fields: ['unit'],
                    data : [
                        {"unit":"PCS"},
                        {"unit":"SET"},
                    ]
                })
          },{
              xtype:'numberfield',
              name:'de_weight',
              id:'de_weight',
              minValue:0,
              allowBlank:false,
              maxLength:10,
              hideTrigger:true,
              fieldLabel:'净重量',
              columnWidth:.3
          },{
              xtype : 'displayfield',
              columnWidth:.1,
              value : '(g/个)'
          },{
              xtype:'textfield',
              name:'de_branduuid',
              id:'de_branduuid',
              hidden:true
          },{
              xtype:'textfield',
              name:'de_brandid',
              id:'de_brandid',
              hidden:true
          },{
              xtype:'textfield',
              name:'de_kindid',
              id:'de_kindid',
              hidden:true,
              listeners:{
                change:me.getProperties,
                scope:this
              }
          },{
              allowBlank: false,
              allowDecimals: true,
              group: 1,
              margin:'10 0 0 0',
              columnWidth:1, 
              border: false,
              html: "<div class='x-form-group-label' id='group1' style='z-index:0;background-color: #bfbfbe;height:22px;width:80%;!important;' title='收拢'><h6>图片和详细说明</h6></div>",
              xtype: "container"
          },
          {
              name:'de_image',
              id:'de_image',
              hidden:true,
              allowBlank:true 
          },{
              id:'fileform',
              xtype:'form',
              bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
              items:[{
                  name:'de_logo',
                  id:'de_logo',
                  xtype: 'filefield',
                  labelStyle:"color:black;",
                  columnWidth:1,
                  name: 'file',
                  fieldLabel:'器件实物图片',
                  //fieldLabel:'<div style="color:blue;padding-left:10px;">品牌Logo，建议图片尺寸在150*90像素左右</div>',
                  //allowBlank: false,
                  buttonOnly:true,
                  group:2,
                  groupName:"图片和详细说明",
                  createFileInput : function() {
	                   var a = this;
	                   a.fileInputEl = a.button.el.createChild({
				           name : a.getName(),
				           cls : Ext.baseCSSPrefix + "form-file-input",
				           tag : "input",
				           type : "file",
				           size : 1,
				          
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
                  value: '建议尺寸750*750像素以上',
                  group:2,
                  groupName:"图片和详细说明",
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
                          field.ownerCt.getForm().submit({
                              url: basePath + 'b2c/product/saveBrandLogo.action',
                              waitMsg:'正在上传',
                              success: function(fp, o){
                                  if(o.result.success){
                                      Ext.Msg.alert('提示','上传成功');  
                                      var logo=Ext.getCmp('logo');
                                      logo.show();
                                      logo.setSrc(o.result.filepath);
                                      Ext.getCmp('de_image').setValue(o.result.filepath);
                                  }else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                              },
                              failure:function(fp,o){
                                  Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                              }
                          });                             
              }
            },{  
                 xtype:'textfield',
                 id:'de_attach',
                 name:'de_attach',
                 hidden:true
            },{
                id:'uploadfileform',
                xtype:'form',
                bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
                items:[{
                    name:'fileupload',
                    id:'fileupload',
                    xtype: 'filefield',
                    labelStyle:"color:black;",
                    columnWidth:1,
                    name: 'file',
                    fieldLabel:'附件',
                    buttonOnly:true,
                    group:1,
                    groupName:"附件",
                    buttonConfig:{
                        text:'上传附件',
                        iconCls:'x-button-icon-pic'
                    },
                    listeners: {
                        change: function(field){
                            field.ownerCt.upload(field);
                        }
                    }
                },{
                    xtype: 'label',
                    id:'path',
                    name:'path',
                    logic:'ignore',
                    margin:'5 0 10 110',
                    html:''
                }],
                 upload: function(field){
                            field.ownerCt.getForm().submit({
                                url: basePath + 'b2c/product/saveFile.action',
                                waitMsg:'正在上传',
                                success: function(fp, o){
                                    if(o.result.success){
                                        Ext.Msg.alert('提示','上传成功');  
                                        var path=Ext.getCmp('path');     
                                        var arr = Ext.getCmp('fileupload').lastValue.split('\\');
                                        path.setText("已上传:"+arr[arr.length-1]);
                                        Ext.getCmp('de_attach').setValue(o.result.filepath);
                                    }else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                                },
                                failure:function(fp,o){
                                    Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                                }
                       });                             
                }
              },{
                 xtype:'htmleditor',
                 labelStyle:"color:black;",
                 fieldLabel:'详细描述',
                 columnWidth:1,
                 height:200,
                 id:'de_description',
                 name:'de_description',
            },{
              allowBlank: false,
              allowDecimals: true,
              alwaysOnTop :false,
              group: 2,
              columnWidth:1,
              margin:'10 0 0 0',
              html: "<div class='x-form-group-label' id='group2' style='z-index:0;background-color: #bfbfbe;height:22px;width:80%;' title='收拢'><h6>器件参数(范围值如只有一个请填写最小值)</h6></div>",
              xtype: "container",
           },{
              xtype:'textfield',
              id:'PropertiesNum',
              name:'PropertiesNum',
              hidden:true
           }],
            buttonAlign:'center',
            buttons:[{
                xtype:'erpSaveButton',
                hidden:_nobutton==1 || !Ext.isEmpty(formCondition)
            },{
                xtype:'erpUpdateButton',
                hidden:_nobutton==1 || Ext.isEmpty(formCondition)
            },{
                xtype:'erpDeleteButton',
                hidden:_nobutton==1 || Ext.isEmpty(formCondition)
            },{
                xtype:'erpSubmitButton',
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
            	iconCls: 'x-button-icon-submit',
            	cls: 'x-btn-gray',
                name:'change',
                hidden:_nobutton==1 || Ext.isEmpty(formCondition)
            },{
                text:'复 制',
                id:'copy',
            	iconCls: 'x-button-icon-submit',
            	cls: 'x-btn-gray',
                name:'copy',
                hidden:_nobutton==1 || Ext.isEmpty(formCondition)
            }]
        }],
    }); 
    me.getData();
    me.callParent(arguments); 
  },
  //点击Dbfind触发的事件
  onCellItemClick:function(){
        var me = this;
        var linkCaller = 'DeviceInApply';
        var status= '';
        var win = new Ext.window.Window({
            id : 'uuWin',
            height : "100%",
            width : "80%",
            maximizable : true,
            closeAction : 'destroy',
            buttonAlign : 'center',
            layout : 'anchor',
            title : '获取编号',
            items : [{
                tag : 'iframe',
                anchor : '100% 100%',
                layout : 'fit',
                html : '<iframe id="iframe_'+linkCaller+'" src="'
                        + basePath
                        + 'jsps/scm/product/getUUidSimple.jsp?type='
                        + linkCaller+'&status='+status
                        + '" height="100%" width="100%" frameborder="0"></iframe>'
                }]
            });
          win.show();   
    },
    getData:function(){
        //从url解析参数
        var me = this;
        if(formCondition != null && formCondition != ''){
            formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
            var me = this;
            Ext.Ajax.request({
                url : basePath + 'b2c/product/getDeviceData.action',
                params: {
                    caller: caller, 
                    id: formCondition.split("=")[1],
                    _noc: (getUrlParam('_noc') || me._noc)
                },
                method : 'post',
                callback : function(options,success,response){
                    var res = new Ext.decode(response.responseText);
                    if(res.exceptionInfo != null){
                        showError(res.exceptionInfo);return;
                    }else{
                       var form = Ext.getCmp('form');
                       var o = {};
                       form.getForm().setValues(res.data);
                       me.setWidgetData(res.data.properties);
                       if(res.data.de_image){
                         var logo=Ext.getCmp('logo');
                         logo.show();
                         logo.setSrc(res.data.de_image);
                       }
                       if(res.data.de_attach){
                           Ext.getCmp('path').getEl().update('<a target="_blank" href="'+res.data.de_attach+'">点击查看</a>');
                       }
                       if(res.data.de_statuscode = 'AUDITED'){
                           form.readOnly = true;
                       }
                  }
                }
            });
        }else if(de_id!=null){
            //审核后修改的时候取数，主要是去掉一些参数
            Ext.Ajax.request({
                 url : basePath + 'b2c/product/getDeviceData.action',
                params: {
                    id: de_id,
                },
                method : 'post',
                callback : function(options,success,response){
                    var res = new Ext.decode(response.responseText);
                    if(res.exceptionInfo != null){
                        showError(res.exceptionInfo);return;
                    }else{
                       var form = Ext.getCmp('form');
                       //因为是根据ID取得数据，以下值都存在，需要先移除才能够保存，否则会重复，相当于新建一张单
                       if(copy_pic_att=='notcopy'){
                    	   delete res.data.de_image;
                           delete res.data.de_attach;
                           delete res.data.de_oldspec;
                       }else{
                    	   delete res.data.de_oldspec;
                       }
                       delete res.data.de_id;
                       delete res.data.de_status;
                       delete res.data.de_auditdate;
                       delete res.data.de_auditman;
                       delete res.data.de_statuscode;
                       delete res.data.de_indate;
                       delete res.data.de_code;
                       form.getForm().setValues(res.data);
                       //将图片展示出来
                       if(res.data.de_image){
                         var logo=Ext.getCmp('logo');
                         logo.show();
                         logo.setSrc(res.data.de_image);
                       }
                       //提示用户已上传过附件
                       if(res.data.de_attach){
                           Ext.getCmp('path').getEl().update('<a target="_blank" href="'+res.data.de_attach+'">点击查看</a>');
                       }
                       if(res.data.de_statuscode = 'AUDITED'){
                           form.readOnly = true;
                       }
                  }
                   me.setWidgetData(res.data.properties);
                }
            });
        }
    },  
    getComboData:function(field){
    	var store=field.getStore();
    	if(!field.hasLoad){
    		store.load();	
    		field.hasLoad=true;
    		getBrand=true;
    	}    
    },
    getOldSpecData:function(field,name){
        var me=this;
        var searchword=Ext.getCmp(name).value.replace(/(^\s*)|(\s*$)/g,"");  
        var sw = {'searchWord':searchword};
        var menu_oldspec=Ext.getCmp('searchMenu_oldspec');
        if(searchword==''||searchword==null){
            if(menu_oldspec){
                menu_oldspec.hide();
                return;
            }
        }
        if(!menu_oldspec){
            menu_oldspec=Ext.create('Ext.menu.Menu', {
                id:'searchMenu_oldspec',
                bodyBorder:false,
                showSeparator:false,
                frame:false,
                width:field.bodyEl.dom.clientWidth,
                renderTo: Ext.getBody(),
                items: [Ext.widget('boundlist',{
                    name:'searchEn',
                    border:false,
                    displayField:'code',
                    renderTo:Ext.getBody(),
                    store:Ext.create('Ext.data.Store', {
                        fields: [ { name:'code'},
                                  { name:'id'  },
                                  { name:'uuid'}],
                                  proxy: {
                                      type: 'ajax',
                                      async: false,
                                      url: basePath+"b2c/product/getOldSpecData.action?caller=DeviceInApply",
                                      extraParams :sw,
                                      reader: {
                                          idProperty:'id',
                                          type: 'json',
                                          root: 'data.data'
                                      },
                                  },
                           autoLoad:false   
                    }),
                    listeners:{
                        itemclick:function(field,record){
                            //点击的时候给隐藏控件赋值
                            Ext.getCmp('de_oldspec').setValue(record.data.code);
                            Ext.getCmp('de_uuid').setValue(record.data.uuid);
                            var mb=me.setLoading();
                            Ext.Ajax.request({
                                url : basePath + 'b2c/product/getDeviceByUUID.action',
                                params: {
                                    UUID: Ext.getCmp('de_uuid').value
                                },
                                method : 'post',
                                callback : function(options,success,response){
                                    mb.close();      
                                    menu_oldspec.hide();
                                    var res = new Ext.decode(response.responseText);
                                    if(res.exceptionInfo != null){
                                        showError(res.exceptionInfo);return;
                                    }else{
                                        var form=Ext.getCmp('form');
                                        if(res.data.de_image){
                                            var logo=Ext.getCmp('logo');
                                            logo.show();
                                            logo.setSrc(res.data.de_image);
                                        }
                                        if(res.data.de_attach){
                                             Ext.getCmp('path').getEl().update('<a target="_blank" href="'+res.data.de_attach+'">点击查看</a>');
                                        }

                                        form.getForm().setValues(res.data);
                                        me.setWidgetData(res.data.properties);
                                    } 
                                }
                            });
                        }
                    }
                })]
            });
            menu_oldspec.mon(Ext.getDoc(),'mousedown', me.collapseIf,menu_oldspec);
        }
         menu_oldspec.down('boundlist').getStore().load({
             params :{
                 searchWord:searchword
             }
         });
         menu_oldspec.alignTo(field.bodyEl, 'tl-bl?');
         menu_oldspec.show();
         Ext.getCmp('de_oldspec').focus(false,true);
    },
    getKindData:function(field,name){
    	var store=field.getStore();
    	store.load({  params :{
             searchWord:field.rawValue
    	}});
    	field.expand();
        getKind=true;
    },
    getProperties:function(field,newValue,oldValue){

        var main = this;
        var kindid=Ext.getCmp('de_kindid').value;
        var form=Ext.getCmp('form');
        var PropertiesNum=Ext.getCmp('PropertiesNum');
        //获取封装规格
//        var de_packingspec=Ext.getCmp('de_packingspec');
//      	de_packingspec.clearValue();
//      	de_packingspec.store.proxy.extraParams.kindid = kindid;
//      	de_packingspec.store.load();
        //动态创建组价
        if(!newValue){
            var fields=Ext.Array.filter(form.items.items,function(item){
                if(item.isProperties) return true;
            });
            Ext.Array.each(fields,function(field){
                form.remove(field);
            });
        }else{
            Ext.Ajax.request({
                url : basePath + 'b2c/product/getProperties.action',
                params: {
                    id: kindid, 
                },
                async: false,
                method :'post',
                timeout:100000,
                callback : function(options,success,response){
                  var res = new Ext.decode(response.responseText);
                  var r=res.data,items=new Array();
                  if(propertiesnum>0){
                      for(var i=0;i<propertiesnum;i++){
                            form.remove('items'+i);
                            form.remove('id'+i);
                      }
                  }
                  console.log(r.data);
                  if(r.data.length>0){
                      propertiesnum=r.data.length;
                      PropertiesNum.setValue(r.data.length);
                  }
                  for(var i=0;i< propertiesnum;i++){
                      var myDate=new Date();
                      var fd = main.newWidget(r.data[i],i);
                      fd.isProperties=true;
                      items.push(fd);
                      var fd = new Ext.form.TextField({id:"id"+i,name:'Properties'+i,value:r.data[i].propertyId,hidden:true});  
                      fd.isProperties=true;
                      items.push(fd);
                  }
                  form.add(items);
                }
            });
        }
		  Ext.MessageBox.show({
		      msg: '正在加载参数,请稍候...',
		      progressText: 'Saving...',
		      width:300,
		      wait:true,
		      waitConfig: {interval:20},
		  });
		  setTimeout(function(){
		      Ext.MessageBox.hide();
		  }, 1000);
     },
     newWidget:function(Properties,i){
              me=this;
              //如果是范围值
              if(Properties.type=="F"){
                 var store= new Ext.data.Store({
                        autoLoad : false,
                        fields : ['id','detno','radix','unit'],
                        data :Properties.multiUnits,
                  })
                  var fd = new Ext.form.Panel({bodyPadding: 5,
                      id:'items'+i,
                      name:'items'+i,
                      type:Properties.type,
                      bodyStyle: 'background:#E8E8E8;font-size:15px',  
                      columnWidth:.5,
                      border:false,
                      layout:'column',
                      items: [{
                          xtype:'textfield',
                          id:Properties.propertyId,
                          name:Properties.propertyId,
                          hidden:true
                       },{
                            xtype:'textfield',
                            id:'detno'+i,
                            name:'detno'+i,
                            value:Properties.detno,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'unit'+i,
                            name:'unit'+i,
                            value:Properties.unit,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'num'+i,
                            name:'num'+i,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'min'+i,
                            name:'min'+i,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'max'+i,
                            name:'max'+i,
                            hidden:true
                        },{
                            xtype: 'label',
                            columnWidth:.3,
                            text: Properties.property.labelCn
                        },{
                            xtype: 'numberfield',
                            id:'F'+i,
                            name:'F'+i,
                            logic:'ignore',
                            hideTrigger:true,
                            columnWidth:.1,
                        },{
                             xtype: 'combo',
                             columnWidth:.15,
                             id:'FF'+i,
                             name:'FF'+i,
                             logic:'ignore',
                             displayField:'unit',
                             valueField:'radix',
                             editable:false,
                             mode: 'local',
                             store : store,
                             listeners : {
                              afterRender : function(combo) {     
                                  var firstitem=store.data.items[0].data.unit;
                                  var firstitemraw=store.data.items[0].data.radix;
                                  combo.setRawValue(firstitem);
                                  combo.setValue(firstitemraw); 
                              }
                           }
                        },{  
                             xtype: 'label',
                             text:' - '
                        },{
                            xtype: 'numberfield',
                            id:'FFF'+i,
                            name:'FFF'+i,
                            logic:'ignore',
                            hideTrigger:true,
                            columnWidth:.1,
                        },{
                            xtype: 'combo',
                            id:'FFFF'+i,
                            name:'FFFF'+i,
                            columnWidth:.15,
                            mode: 'local',
                            logic:'ignore',
                            displayField:'unit',
                            valueField:'radix',
                            editable:false,
                            store : store,
                            listeners : {
                             afterRender : function(combo) {     
                                  var firstitem=store.data.items[0].data.unit;
                                  var firstitemraw=store.data.items[0].data.radix;
                                  combo.setRawValue(firstitem);
                                  combo.setValue(firstitemraw); 
                              }
                           }
                        }],
                   })
              }
              //如果输入的内容是数字
              if(Properties.type=="N"){
                  var store=new Ext.data.Store({
                        autoLoad : false,
                        fields : ['id','detno','radix','unit'],
                        data :Properties.multiUnits,
                  });
                  var fd = new Ext.form.Panel({bodyPadding: 5,  // Don't want content to crunch against the borders
                    id:'items'+i,
                    name:'items'+i,
                    type:Properties.type,
                    bodyStyle: 'background: #E8E8E8;font-size:15px',
                    border:false,
                    columnWidth:.5,
                    layout:'column',
                    items: [{
                      xtype:'textfield',
                      id:Properties.propertyId,
                      name:Properties.propertyId,
                      hidden:true
                    },{
                        xtype:'textfield',
                        id:'detno'+i,
                        name:'detno'+i,
                        value:Properties.detno,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'unit'+i,
                        name:'unit'+i,
                        value:Properties.unit,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'num'+i,
                        name:'num'+i,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'min'+i,
                        name:'min'+i,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'max'+i,
                        name:'max'+i,
                        hidden:true
                    },{
                        xtype: 'label',
                        columnWidth:.3,
                        text: Properties.property.labelCn
                    },{
                        xtype: 'numberfield',
                        id:'N'+i,
                        name:'N'+i,
                        logic:'ignore',
                        allowDecimals: true,
                        hideTrigger:true,
                        columnWidth:.35, 
                    },{
                         xtype: 'combo',
                         columnWidth:.15,
                         id:'NN'+i,
                         name:'NN'+i,
                         logic:'ignore',
                         displayField:'unit',
                         valueField:'radix',
                         editable:false,
                         mode: 'local',
                         store : store,
                         listeners : {
                              afterRender : function(combo) { 
                                  //设置初始值
                                  var firstitem=store.data.items[0].data.unit;
                                  var firstitemraw=store.data.items[0].data.radix;
                                  combo.setRawValue(firstitem);
                                  combo.setValue(firstitemraw); 
                            }
                        }
                    }]
                });
              }
              //如果是下拉框
              if(Properties.type=="S"){
                  var fd = new Ext.form.Panel({bodyPadding: 5, 
                      id:'items'+i,
                      name:'items'+i,
                      type:Properties.type,
                      bodyStyle: 'background: #E8E8E8;font-size:15px',
                      columnWidth:.5,  
                      border:false ,
                      layout:'column',
                        items: [{
                          xtype:'textfield',
                          id:Properties.propertyId,
                          name:Properties.propertyId,
                          hidden:true
                        },{
                            xtype:'textfield',
                            id:'detno'+i,
                            name:'detno'+i,
                            value:Properties.detno,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'unit'+i,
                            name:'unit'+i,
                            value:Properties.unit,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'num'+i,
                            name:'num'+i,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'min'+i,
                            name:'min'+i,
                            hidden:true
                        },{
                            xtype:'textfield',
                            id:'max'+i,
                            name:'max'+i,
                            hidden:true
                        },{
                            xtype: 'label',
                            columnWidth:.3,
                            text: Properties.property.labelCn
                        },{
                         xtype: 'combo',
                         columnWidth:.5,
                         id:'S'+i,
                         name:'S'+i,
                         logic:'ignore',
                         editable:true,
                         mode: 'local',
                         displayField:'option',
                         store : new Ext.data.Store({
                            autoLoad : false,
                            fields : ['id','option','detno'],
                            data :Properties.options,
                         }),
                         listeners:{
                             afterRender : function(combo) { 
                                 //设置初始值
                                 var firstitem=combo.store.data.items[0].data.option;
                                 combo.setValue(firstitem);
                           }
                         }
                      }]
                  });
              }
              //如果是联想词
              if(Properties.type=="A"){  
                  var fd = new Ext.form.Panel({bodyPadding: 5,  
                    id:'items'+i,
                    name:'items'+i,
                    type:Properties.type,
                    bodyStyle: 'background: #E8E8E8;font-size:15px',
                    columnWidth:.5,
                    border:false ,
                    layout:'column',
                    items: [{
                      xtype:'textfield',
                      id:Properties.propertyId,
                      name:Properties.propertyId,
                      hidden:true
                    },{
                        xtype:'textfield',
                        id:'detno'+i,
                        name:'detno'+i,
                        value:Properties.detno,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'unit'+i,
                        name:'unit'+i,
                        value:Properties.unit,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'num'+i,
                        name:'num'+i,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'min'+i,
                        name:'min'+i,
                        hidden:true
                    },{
                        xtype:'textfield',
                        id:'max'+i,
                        name:'max'+i,
                        hidden:true
                    },{
                        xtype: 'label',
                        columnWidth:.3,
                        text: Properties.property.labelCn
                    },{
                        logic:'ignore',
                        xtype: 'combo',
                        id:'A'+i,
                        name:'A'+i,
                        columnWidth:.5,
                        displayField:'propertyValue',
                        onTriggerClick:function(field){
                        	this.ownerCt.ownerCt.ownerCt.getATypeAssociation(Properties.kindId,Properties.property.id,'A'+i);
                        }
                    }]
               });
              }
              return fd;
        },
        setWidgetData:function(data){
        
            for(var i=0;i<data.length;i++){
                var myPanel=Ext.getCmp("items"+i);
                var myPanelLength=myPanel.items.length;
                var propertiesid=myPanel.items.items[0].id;
                for(var j=0;j<data.length;j++){
                    if(propertiesid==data[j].propertyId){
                        //stringValue为空表示该输入框没有值
                        if(data[j].stringValue==''||data[j].stringValue==null){
                               break;
                        }
                        //范围值和数字类型均为空表示用户输入的任意字符或者下拉(A)(S)
                        if(data[j].max==null&&data[j].min==null&&data[j].numberic==null&&data[j].unit==null){
                            myPanel.items.items[myPanelLength-1].setValue(data[j].stringValue);
                        }
                        //范围值为空，数字值不为空表示Num类型(N)
                        if(data[j].max==null&&data[j].min==null&&data[j].numberic!=null){
                            str=data[j].stringValue;
                            var unit=null
                            var value = str.replace(/[a-zA-Z]/g,"");
                            //处理负数，先去除英文，然后判断第一位的符号，之后取数字，根据数字长度截取字符串长度
                            if(value.substr(0,1)=='-'){
                                unit= str.replace(/[^\d\.]/ig,"");
                                unit= str.substring(unit.length+1);
                                value='-'+str.replace(/[^\d\.]/ig,"");
                            }else{
                            	value=str.replace(/[^\d\.]/ig,"");//直接取出数字
                                unit= str.substring(value.length);
                            }
                            var unitval = myPanel.items.items[myPanelLength-1].store.findRecord('unit', unit);
                            myPanel.items.items[myPanelLength-2].setValue(value);
                            myPanel.items.items[myPanelLength-1].setValue(unitval.get('radix'));
                        }
                        //范围值不为空(F)
                        if(data[j].max!=null||data[j].min!=null){
                            var strs= new Array();
                            var reg = /(\d+)(\w+)/;
                            //如果用户在范围值里面只输入了一个
                            if(data[j].stringValue.indexOf('~')<0){
                                var min = data[j].stringValue.replace(/[^0-9]/ig,""); 
                                var minunit= data[j].stringValue.substring(min.length);
                                myPanel.items.items[myPanelLength-5].setValue(min); 
                                myPanel.items.items[myPanelLength-4].setRawValue(minunit);  
                            }
                            else{
                                strs=data[j].stringValue.split('~');
                                var min = strs[0].replace(/[a-zA-Z]/g,""); 
                                var max = strs[1].replace(/[a-zA-Z]/g,""); 
                                var minunit=null;
                                var maxunit=null;
                                //判断正负
                                if(min.substr(0,1)=='-'){
                                	//取出数字，在将数字和负号的长度截取单位
                                    minunit= strs[0].replace(/[^\d\.]/ig,"");//取出数字
                                    minunit= strs[0].substring(minunit.length+1);//数字长度+1截取单位
                                    min='-'+strs[0].replace(/[^\d\.]/ig,"");//取出数字
                                }else{
                                	min=strs[0].replace(/[^\d\.]/ig,"");//直接取出数字
                                	minunit= strs[0].substring(min.length); 
                                }
                                if(max.substr(0,1)=='-'){
                                    maxunit= strs[1].replace(/[^\d\.]/ig,"");
                                    maxunit= strs[1].substring(maxunit.length+1);
                                    max='-'+strs[1].replace(/[^\d\.]/ig,"");
                                }else{
                                    max= strs[1].replace(/[^\d\.]/ig,""); 
                                    maxunit=strs[1].substring(max.length); 
                                }
                                myPanel.items.items[myPanelLength-5].setValue(min); 
                                //通过数据源直接给单位的下拉框赋值
                                var minvalr = myPanel.items.items[myPanelLength-4].store.findRecord('unit', minunit);
                                myPanel.items.items[myPanelLength-4].setValue(minvalr.get('radix'));
                                myPanel.items.items[myPanelLength-2].setValue(max); 
                                var maxvalr = myPanel.items.items[myPanelLength-1].store.findRecord('unit', maxunit);
                                myPanel.items.items[myPanelLength-1].setValue(maxvalr.get('radix'));
                            }   
                        }
                    }
                }
            }
        },
        getATypeAssociation:function(kindid,id,widgetid){
            widgetID= widgetid;
            Ext.Ajax.request({
                async: false,
                url: basePath+"b2c/product/getATypeAssociation.action?caller=DeviceInApply",
                params: {
                    kindid: kindid, 
                    id:id,
                    searchword:'',
                    shownum:50
                },
                method :'post',
                callback : function(options,success,response){
                  var res = new Ext.decode(response.responseText);
                  Ext.getCmp(widgetID).store=new Ext.data.Store({
                      autoLoad : true,
                      fields : ['propertyValue'],
                      data :res.data.data,
                   });
                  Ext.getCmp(widgetID).expand();
                }
            });           
        },
        setLoading:function(){
            var mb = new Ext.window.MessageBox();
            mb.wait('正在载入数据','请稍后...',{
               interval: 10000, 
               duration: 100000,
               increment: 1000,
               scope: this
            });
            return mb;
        },
        collapseIf: function(e){
            var me = this;
            if (!me.hidden && !e.within(me.inputEl, false, true) && !e.within(me.el, false, true)) {
                me.hide();
            }
        },
});