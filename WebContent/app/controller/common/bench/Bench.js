Ext.QuickTips.init();
Ext.define('erp.controller.common.bench.Bench', {
	extend: 'Ext.app.Controller',
	views:['common.bench.Bench','common.bench.BenchFormPanel','common.bench.BusinessFormPanel','common.bench.SwitchButton','core.button.StatButton1',
		'erp.view.common.bench.FlowChart','common.bench.SceneFormPanel'
	],
  	init:function(){
   		this.control({
   			'erpBenchFormPanel erpSwitchButton':{
	   			change:function(swi,business){
	   				var businesses = Ext.getCmp('businesses');
	   				var Business = business.data.bb_code;
	   				var businessid = 'business_'+Business;
	   				var businessPanel = businesses.down('#'+businessid);
	   				var scenes = businessPanel.down('erpBusinessFormPanel erpSwitchButton');
	   				if(scene!=null || !scenes.getActive()){
	   					var btn = scenes.down('#'+(scene || business.data.active));
	   					scenes.setActive(btn);
	   					scene = null;
	   				}
	   				
	   				businesses.layout.setActiveItem(businessid);
	   			}
	   		},
	   		'erpBusinessFormPanel erpSwitchButton':{
	   			change:function(swi,scene){
	   				var scenes = swi.up('erpBusinessFormPanel').nextSibling();
	   				var Scene = scene.data.bs_code;
	   				var caller = scene.data.bs_caller;
	   				var sencecode = 'scene_'+Scene;
	   				var scenePanel = scenes.down('#'+sencecode);
	   				if(!scenePanel){
	   					var url = '', style='';
	   					if(scene.data.bs_islist==-1){
	   						url = 'jsps/common/datalist.jsp?whoami=' + caller;
	   						//style = 'padding-top:10px;';
	   					}else{
	   						url = 'jsps/common/bench/scene.jsp?Scene=' + Scene;
	   					}
	   					if(scene.data.bs_fixcond){
	   						url += '&'+scene.data.bs_fixcond;
	   					}
	   					if(!contains(url,'urlcondition',true)){
	   						var urlcondition = getUrlParam('urlcondition');
	   						if(urlcondition)
	   							url += '&urlcondition='+urlcondition;
	   					}
	   					if(!contains(url,'_noc',true)){
	   						var _noc = getUrlParam('_noc');
	   						if(_noc)
	   							url += '&_noc='+_noc;
	   					}
	   					if(!contains(url,'_config',true)){
	   						var _config = getUrlParam('_config');
		   					if(_config){
		   						url += '&_config='+_config;
		   					}
	   					}
	   					url = parseUrl(url);
	   					if(scene.data.bs_islist==-1){
	   						scenes.add(Ext.create('Ext.container.Container',{
	   							layout: 'border', 
	   							id:sencecode,
								hideBorders: true, 
								items: [{
									region:'north',
									xtype:'erpSceneFormPanel',
									Scene: Scene,
									isList: true
								},{
									region:'center',
			   						xtype: 'panel',
			   						tag : 'iframe',
									border : false,
									style:'height:100%',
									bodyStyle:'height:100%',
									layout : 'fit',
									html : '<iframe id="iframe_'+sencecode+'" src="'+basePath+url+'" height="100%" width="100%" style="'+style+'" frameborder="0" scrolling="auto"></iframe>'
								}]
							}));
	   					}else{
	   						scenes.add({
		   						xtype: 'panel',
		   						tag : 'iframe',
								border : false,
								id:sencecode,
								style:'height:100%',
								bodyStyle:'height:100%',
								layout : 'fit',
								html : '<iframe id="iframe_'+sencecode+'" src="'+basePath+url+'" height="100%" width="100%" style="'+style+'" frameborder="0" scrolling="auto"></iframe>'
							});
	   					}
	   				}
	   				scenes.layout.setActiveItem(sencecode);
	   			}
	   		}
		});
   	}
});