Ext.apply(Ext.util.Format, {
	number: function(v, formatString) {
		var formatCleanRe  = /[^\d\.]/g;
        if (!formatString) {
            return v;
        }
        v = Ext.Number.from(v, NaN);
        if (isNaN(v)) {
            return '';
        }
        var comma = Ext.util.Format.thousandSeparator,
            dec   = Ext.util.Format.decimalSeparator,
            i18n  = false,
            neg   = v < 0,
            hasComma,
            psplit;

        v = Math.abs(v);

        // The "/i" suffix allows caller to use a locale-specific formatting string.
        // Clean the format string by removing all but numerals and the decimal separator.
        // Then split the format string into pre and post decimal segments according to *what* the
        // decimal separator is. If they are specifying "/i", they are using the local convention in the format string.
        if (formatString.substr(formatString.length - 2) == '/i') {
            if (!I18NFormatCleanRe) {
                I18NFormatCleanRe = new RegExp('[^\\d\\' + UtilFormat.decimalSeparator + ']','g');
            }
            formatString = formatString.substr(0, formatString.length - 2);
            i18n   = true;
            hasComma = formatString.indexOf(comma) != -1;
            psplit = formatString.replace(I18NFormatCleanRe, '').split(dec);
        } else {
            hasComma = formatString.indexOf(',') != -1;
            psplit = formatString.replace(formatCleanRe, '').split('.');
        }

        if (1 < psplit.length) {
            v = v.toFixed(psplit[1].length);
        } else if(2 < psplit.length) {
            //<debug>
            Ext.Error.raise({
                sourceClass: "Ext.util.Format",
                sourceMethod: "number",
                value: v,
                formatString: formatString,
                msg: "Invalid number format, should have no more than 1 decimal"
            });
            //</debug>
        } else {
            v = v.toFixed(0);
        }

        var fnum = v.toString();

        psplit = fnum.split('.');

        if (hasComma) {
            var cnum = psplit[0],
                parr = [],
                j    = cnum.length,
                m    = Math.floor(j / 3),
                n    = cnum.length % 3 || 3,
                i;

            for (i = 0; i < j; i += n) {
                if (i !== 0) {
                    n = 3;
                }

                parr[parr.length] = cnum.substr(i, n);
                m -= 1;
            }
            fnum = parr.join(comma);
            if (psplit[1]) {
                fnum += dec + psplit[1];
            }
        } else {
            if (psplit[1]) {
                fnum = psplit[0] + dec + psplit[1];
            }
        }

        if (neg) {
            /*
             * Edge case. If we have a very small negative number it will get rounded to 0,
             * however the initial check at the top will still report as negative. Replace
             * everything but 1-9 and check if the string is empty to determine a 0 value.
             */
            neg = fnum.replace(/[^1-9]/g, '') !== '';
        }

        return (neg ? '-' : '') + formatString.replace(/[\d,?\.?]+/, fnum);
    }
});

Ext.define('erp.view.fa.RepQuery',{ 
	extend: 'Ext.Viewport',
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype: 'form',
	    	  id: 'form',
	    	  anchor: '100% 14%',
	    	  layout: 'hbox',
	    	  bodyStyle: 'background: #f7f7f7;',
	    	  fieldDefaults: {
	    		margin: '3 2 3 8',
	    		cls: 'form-field-allowBlank'
	    	  },
	    	  items: [
	    	  	{
	    		  xtype: 'dbfindtrigger',
	    		  fieldLabel: '报表编号',
	    		  id: 'fs_code',
	    		  name: 'fs_code',
	    		  logic: 'fs_name',
	    		  margin: '3 0 3 8'
	    	  },{
	    	  	  fieldLabel:null,
	    		  xtype: 'textfield',
	    		  readOnly: true,
	    		  id: 'fs_name',
	    		  fieldStyle: 'background: #f1f1f1;',
	    		  name: 'fs_name',
	    		  hideTrigger: true,
	    		  margin: '3 2 3 0'
	    	  },{
	    		  xtype: 'textfield',
	    		  hidden: true,
	    		  id: 'fs_title1'
	    	  },{
	    		  xtype: 'textfield',
	    		  hidden: true,
	    		  id: 'fs_title2'
	    	  },{
	    		  xtype: 'textfield',
	    		  hidden: true,
	    		  id: 'fs_righttitle1'
	    	  },{
	    		  xtype: 'textfield',
	    		  hidden: true,
	    		  id: 'fs_righttitle2'
	    	  },{
	    		  xtype: 'textfield',
	    		  hidden: true,
	    		  id: 'fs_head'
	    	  },{
	    		  xtype: 'textfield',
	    		  hidden: true,
	    		  id: 'fs_righthead'
	    	  },{
	    		  xtype: 'monthdatefield',
	    		  fieldLabel: '期间',
	    		  id: 'frd_yearmonth',
	    		  name: 'frd_yearmonth'
	    	  },{
	    		  xtype: 'textfield',
	    		  fieldLabel: '期间',
	    		  hidden: true,
	    		  id: 'addate',
	    		  name: 'addate'
	    	  }],
	    	  tbar: [{
	    	  	  margin:0,
	    		  xtype: 'erpQueryButton' 
	    	  },
	    	  //2018070033 问题反馈去除报表查询界面打印按钮
	    	  /*{
	    	  	  margin: '0 0 0 5',
	    		  xtype: 'erpPrintButton'
	    	  }*/{
	    		  name: 'export',
	    		  text: $I18N.common.button.erpExportButton,
	    		  iconCls: 'x-button-icon-excel',
	    		  cls: 'x-btn-gray',
	    		  margin: '0 0 0 5'
	    	  }, '->', {
	    		  xtype: 'erpCloseButton',
	    		  margin: '0 0 0 5'
	    	  }]
	      }, {
	    	  xtype: 'gridpanel',  
	    	  anchor: '100% 86%',
	    	  columnLines: true,
	    	  columns: [{
	    		  text: '列1',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列2',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列3',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列4',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  },{
	    		  text: '列5',
	    		  cls: 'x-grid-header-1',
	    		  flex: 1
	    	  }],
	    	  store: new Ext.data.Store({
	    		  fields: [],
	    		  data: [{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}]
	    	  })
	      }]
		});
		me.callParent(arguments); 
	}
});