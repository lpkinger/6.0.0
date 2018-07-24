Ext.define('erp.view.pm.source.SourceForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.SourceFormPanel',
	id: 'sourceform', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil:Ext.create('erp.util.BaseUtil'),
    layout:'column',
    defaults:{
    columnWidth:'0.2'
    },
    items: [
    {
     xtype: 'combo',
     fieldLabel: '来源类型',
     labelAlign:'right',
     //labelWidth:80,
     id:'type',
     allowBlank:false,
     fieldStyle:{
     background:'#fffac0',
     color:'#515151'
     },
     listConfig:{
     emptyText: "未找到匹配值",
     maxHeight: 80},
     displayField:'type',
     valueField:'caller',
     editable : false,
	 queryMode : "local",
     //value:'MRPSSaleM',
     value : getUrlParam('isSaleForecast')=='true'?'MRPSForeCastM':'MRPSSaleM',
     store:{
        fields:['type','caller'],
        data:[
         {type:'销售订单',caller:'MRPSSaleM'},
        {type:'销售预测',caller:'MRPSForeCastM'}
             ]      
         },                      
    },
    {
      xtype:'condatefield',
      labelAlign:'right',
      fieldLabel:'需求时间',
      name:'condate',
      id:'condate',
      columnWidth:'0.5',
      fieldStyle:'background:#FFFAFA;color:#515151;'
    },{
       xtype:'checkbox',
       labelAlign:'right',
       columnWidth:'0.3',
       name:'detail',
       id:'detail',
       fieldLabel:'显示物料明细',
    },
    {
       columnWidth:'0.2',
       labelAlign:'right',
 	   fieldLabel:'记录条数',
 	   id:'datacount',
 	   labelStyle:'font-size:16px;font-weight: bold;',
 	   fieldStyle : 'background:#f0f0f0 ;border-bottom-style:1px solid;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none;font-weight: bold; ',
    }
	],
    tbar: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	id:'querybutton'
	}, '->', {
    	xtype: 'erpLoadButton',
    	text: $I18N.common.button.erpLoadButton,
    	listeners:{
            afterrender:function(btn){
              var value=getUrlParam('keyValue');
              if(value==null){
              btn.hide();
              }
            }      	   	
    	} 
    	
    },'-',{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(b){
    		var form = b.ownerCt.ownerCt, grid = form.ownerCt.down('grid');
    		grid.BaseUtil.createExcel(caller, 'datalist', form.getCondition());
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	},
	ChangeGridByCaller:function(caller){
	
	
	},
	getFields: function(tn, fields, con){
		var des = '';
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/getFields.action",
        	params: {
        		tablename: tn,
        		field: fields,
    			condition: con
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success && res.data != null){
        			des = res.data;
        		}
        	}
		});
		return des;
	},
	getCondition: function() {
		var rang = this.down('#condate').value, condition = ''; 
		if( !Ext.isEmpty(rang) ){
			if (caller.indexOf('ForeCast') > 0)    				  
				condition = "  ( sd_startdate " + rang + ") AND ";
			else 
				condition = "( sd_delivery  " + rang + " ) AND ";
		}
		var orderKind = caller.indexOf("Sale") > 0 ? 'SALE' : 'FORECAST';
		var sourcecode = this.getFields("MPSMain","mm_sourcecode","mm_id="+keyValue);
		if (sourcecode && sourcecode!='' && sourcecode!=' ' && sourcecode!='ALL' && sourcecode!='全部' ){
			if(OrderKind=='SALE'){
				condition=condition+" and sa_cop='"+sourcecode+"' ";
			}else if(OrderKind=='FORECAST'){
				condition=condition+" and sf_cop='"+sourcecode+"' ";
			} 
		}
		if ( kind == 'MDS' ) {
			condition += " sd_id not in (select mdd_sdid from mdsdetail where mdd_orderkind='" 
				+ orderKind + "' AND mdd_mainid=" + keyValue + ") ";
		} else {
			condition += " sd_id not in (select md_sdid from mpsdetail where md_orderkind='" 
				+ orderKind + "' AND md_mainid=" + keyValue + ") ";
		}
		return condition;
	}
});