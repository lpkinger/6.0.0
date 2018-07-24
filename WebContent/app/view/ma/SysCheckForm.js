Ext.define('erp.view.ma.SysCheckForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.SearChForm',
	id: 'SearChForm', 
	layout:'column',
	region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',	       
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	GridUtil:Ext.create('erp.util.GridUtil'),
	defaults: { 
    columnWidth:0.5
     },
	items: [{
     columnWidth:0.25,
     name:'emname',
     id:'emname',
	 xtype:'dbfindtrigger',
	 fieldStyle:'background:#EBEBEB',
	 fieldLabel:'人员名称',
	 labelStyle:'font-size:14px;font-color:#CDC9C9',
	},{
	    columnWidth:0.5,
	    name:'recorddate',
	    id:'date',
        xtype:'condatefield',
		fieldLabel:'时间区间',
		labelAlign:'right',
		fieldStyle:'background:#EBEBEB',
		labelStyle:'font-size:14px;font-color:#CDC9C9',
    },{
       xtype: 'button',
       text:'查询',
       id:'scan',
       columnWidth:0.1,
       cls: 'x-btn-gray',
       iconCls: 'x-button-icon-scan',
       style:'margin-left:20px;background:#f0f0f0',
       handler:function(btn){
    	   var sysgrid=Ext.getCmp('sysgrid');
    	   sysgrid.hide();
    	   var grid=Ext.getCmp('gridpanel');
    	   var condition="";
    	   var value=Ext.getCmp('emname').getValue();
    	   if(value){
    		   condition="scd_emname='"+value+"' AND scd_indate "+Ext.getCmp('date').value;
    	   }
    	   grid.show();
    	   var params={
    			   caller:'SysCheckData',
    			   condition:condition
    	   };
    	  btn.ownerCt.GridUtil.loadNewStore(grid,params);
       }
    },{
        xtype: 'button',
        text:'更多操作',
        cls: 'x-btn-gray',
        id:'more',
        columnWidth:0.15,
        iconCls: 'x-button-icon-execute',
        style:'margin-left:20px;background:#f0f0f0',
        menu: [{text: '查看详细',iconCls: 'x-button-icon-save',id:'seedetails',handler:function(){
        	var sysgrid=Ext.getCmp('sysgrid');
        	sysgrid.hide();
        	var grid=Ext.getCmp('gridpanel');
        	grid.show();
        	var params={caller:'SysCheckData',condition:"scd_indate "+Ext.getCmp('date').value};
        	Ext.getCmp('SearChForm').GridUtil.loadNewStore(grid,params);
        	
         }},
         {text:'生成处罚单',iconCls:'x-button-icon-submit',id:'turnpunish',handler:function(){
        	 var form=Ext.getCmp('SearChForm');
        	 var data=form.getParamData();
        	 if(data.length<1){
        		 showError('无有效数据!查看明细界面再生成!');
        		 return
        	 }else{
				Ext.Ajax.request({
					url:basePath+'ma/SysCheck/TurnReandpunish.action',
			        method:'post',
			        params:{
			        	data:unescape(Ext.JSON.encode(data).replace(/\\/g,"%"))
			        },
			        callback:function(options,success,response){
			        	var local=new Ext.decode(response.responseText);
			        	if(local.exceptionInfo){
			        		showError(local.exceptionInfo);
			        		return
			        	}
			        	if(local.success){
			        		var grid=Ext.getCmp('gridpanel');
			        		form.GridUtil.loadNewStore(grid,{caller:'SysCheckData',condition:"scd_indate "+Ext.getCmp('date').value});
			        		Ext.Msg.alert('提示','生成成功!');
			        		
			        	}
			        }
				});
        	 }
         }},{
        	 text:'生成运算数据',iconCls:'x-button-icon-save',id:'rundocheck',handler:function(){
        		 var main = parent.Ext.getCmp("content-panel");
					if(!main)
						main = parent.parent.Ext.getCmp("content-panel");
					if(main){
						main.getActiveTab().setLoading(true);//loading...
					}
        		 Ext.Ajax.request({
 					url:basePath+'ma/SysCheck/RunCheck.action',
 			        method:'post',
 			        params:{
 			        },
 			        callback:function(options,success,response){
 			        	main.getActiveTab().setLoading(false);
 			        	var local=new Ext.decode(response.responseText);
 			        	if(local.exceptionInfo){
 			        		showError(local.exceptionInfo);
 			        		return
 			        	}
 			        	if(local.success){
 			        		var grid=Ext.getCmp('gridpanel');
 			        		var form=Ext.getCmp('SearChForm');
 			        		form.GridUtil.loadNewStore(grid,{caller:'SysCheckData',condition:"scd_indate "+Ext.getCmp('date').value});
 			        		Ext.Msg.alert('提示','生成成功!');
 			        		
 			        	}
 			        }
 				});
        	 }}]
     }],
	initComponent : function(){ 
		this.callParent(arguments);
	},
	getParamData:function(){
		var grid=Ext.getCmp('gridpanel');
	    var items=grid.store.data.items;
	    var data=new Array();
	    console.log(items);
	    for(var i=0;i<items.length;i++){
	    	var o=new Object();
	    	if(items[i].data.scd_ispunished==0&&items[i].data.scd_id!=0&&items[i].data.scd_id!='0'&&items[i].data.scd_method==-1){
	    		o.scd_id=items[i].data.scd_id;
	    		o.scd_punishamount=items[i].data.scd_punishamount;
	    		o.scd_title=items[i].data.scd_title;
	    		o.scd_emname=items[i].data.scd_emname;
	    		o.scd_sourcecode=items[i].data.scd_sourcecode;
	    		o.scd_url=items[i].data.scd_url;
	    		o.scd_keyfield=items[i].data.sf_keyfield;
	    		o.scd_mainfield=items[i].data.sf_mainfield;
	    		o.scd_sourceid=items[i].data.scd_sourceid;
	    		data.push(o);
	    	}
	    }
	    return  data;
	}
});