Ext.define('erp.view.core.button.MRPLoad',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMRPLoadButton',
		iconCls: 'x-button-icon-Consign',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpMRPLoadButton,
    	id: 'consign',
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
        menu: [{
			iconCls: 'main-msg',
	        text: '装载来源',
	        listeners: {
	        	 afterrender: function(btn){
  				   var status = Ext.getCmp("mm_statuscode");
  				   if(status && status.value != 'ENTERING'){
  					   btn.hide();
  				   }
  			   },
  			   click:function(btn){
  				   var form=Ext.getCmp('form');
  				   var keyField=form.keyField;
  				   var KeyValue=Ext.getCmp(keyField).value;
  				   var MainCode=Ext.getCmp(form.codeField).value;
  				   var isSaleForecast = false;
  				   var ismrpSeparateFactory = false;
  				   if(KeyValue==null||KeyValue==''){
  					    //为新增,自动执行保存按钮的业务逻辑，保存成功后刷新界面后再执行装载按钮逻辑
	    		      form.BaseUtil.getRandomNumber();	
	    		      form.FormUtil.getSeqId(form);
	    		      KeyValue=Ext.getCmp(keyField).value;
			    	  MainCode=Ext.getCmp(form.codeField).value;
			    	  form.FormUtil.onSave();
  				   }
  				   form.BaseUtil.getSetting('MpsMain','defaultSaleforecast',function(bool){
  					   if(bool){
  						 isSaleForecast = true;
  					   }
  				   },false);
  				   form.BaseUtil.getSetting('MpsDesk','mrpSeparateFactory',function(bool){
					   if(bool){
						   ismrpSeparateFactory = true;
					   }
				   },false);
  				   var url=basePath+"jsps/pm/source/Source.jsp";   
  				   var panel = Ext.getCmp("sourceid=" +KeyValue);                       
  				   var main = parent.Ext.getCmp("content-panel");
  				   var kind=getUrlParam('kind');
  				   var panelId= main.getActiveTab().id;
  				   main.getActiveTab().currentGrid = Ext.getCmp('grid');
  				   if(!panel){ 
  					   var title = "";
  					   if (KeyValue.toString().length>4) {
  						   title = KeyValue.toString().substring(KeyValue.toString().length-4);	
  					   } else {
  						   title = KeyValue;
  					   }
  					   panel = { 
  							   title:'来源查询('+KeyValue+')',
  							   tag : 'iframe',
  							   tabConfig:{tooltip:'来源查询('+title+')'},
  							   frame : true,
  							   border : false,
  							   layout : 'fit',
  							   iconCls : 'x-tree-icon-tab-tab',
  							   html : '<iframe id="iframe_maindetail_'+caller+"_"+KeyValue+'" src="'+url+'?keyValue='+KeyValue+'&maincode='+MainCode+'&kind='+kind+'&panelId='+panelId+'&isSaleForecast='+isSaleForecast+'&ismrpSeparateFactory='+ismrpSeparateFactory+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
  							   closable : true,
  							   listeners : {
  								   close : function(){
  									   main.setActiveTab(main.getActiveTab().id); 
  								   }
  							   } 
  					   };
  					    openTab(panel,"sourceid=" + KeyValue); 
  				   }else{ 
  					   main.setActiveTab(panel); 
  				   } 
  			   }
	        }
	    },{
	    	iconCls: 'main-msg',
	        text:'装载排程',
	        listeners: {
	        	click: function(m){
	        		var keyValue = Ext.getCmp('mm_id').value;
	        		var form=Ext.getCmp('form');
	        		if(keyValue==null||keyValue==''){
  					    //为新增,自动执行保存按钮的业务逻辑，保存成功后刷新界面后再执行装载按钮逻辑
	    		      form.BaseUtil.getRandomNumber();	
	    		      form.FormUtil.getSeqId(form);
	    		      keyValue = Ext.getCmp('mm_id').value;
			    	  form.FormUtil.onSave();
  				    }
  				   var url=basePath+"jsps/pm/mps/loadSaleDetailDet.jsp";   
  				   var panel = Ext.getCmp("sourceid=" +keyValue+"-");      
  				   var main = parent.Ext.getCmp("content-panel");
  				   var panelId= main.getActiveTab().id;
  				   main.getActiveTab().currentGrid = Ext.getCmp('grid');
  				   if(!panel){ 
  					   var title = "";
  					   if (keyValue.toString().length>4) {
  						   title = keyValue.toString().substring(keyValue.toString().length-4);	
  					   } else {
  						   title = keyValue;
  					   } 					   
  					   panel = { 
  							   title:'销售排程装载('+keyValue+')',
  							   tag : 'iframe',
  							   tabConfig:{tooltip:'销售排程装载('+title+')'},
  							   frame : true,
  							   border : false,
  							   layout : 'fit',
  							   iconCls : 'x-tree-icon-tab-tab',
  							   html : '<iframe id="iframe_maindetail_'+caller+"_"+keyValue+'" src="'+url+'?type=MPS&keyValue='+keyValue+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
  							   closable : true,
  							   listeners : {
  								   close : function(){
  									   main.setActiveTab(main.getActiveTab().id); 
  								   }
  							   } 
  					   };
  					    openTab(panel,"sourceid=" + keyValue+"-"); 
  				   }else{ 
  					   main.setActiveTab(panel); 
  				   } 
	        		/*var win = new Ext.window.Window({
				    	id : 'win',
	   				    height: "100%",
	   				    width: "95%",
	   				    maximizable : true,
	   				    title:'销售排程装载',
	   					buttonAlign : 'center',
	   					layout : 'anchor',
	   					draggable:false, 
	   				    items: [{
	   				    	  tag : 'iframe',
	   				    	  frame : true,
	   				    	  anchor : '100% 100%',
	   				    	  layout : 'fit',
	   				    	 html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/pm/mps/loadSaleDetailDet.jsp?type=MPS&keyValue='+keyValue 
	   				    	  	+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	   				    }],
	   				    buttons : [{
	   				    	text : $I18N.common.button.erpCloseButton,
	   				    	iconCls: 'x-button-icon-close',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(){
	   				    		Ext.getCmp('win').close();
	   				    	}
	   				    }]
	   				});
	   				win.show();*/
	        	}
	        }
	    }],
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});