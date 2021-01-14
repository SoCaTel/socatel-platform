/* ------------------------------------------------------------------------------
 *
 *  # Template JS core
 *
 *  Includes minimum required JS code for proper template functioning
 *
 * ---------------------------------------------------------------------------- */


// Setup module
// ------------------------------

var App = function () {


    //
    // Setup module components
    //

    // Transitions
    // -------------------------

    // Disable all transitions
    var _transitionsDisabled = function() {
        $('body').addClass('no-transitions');
    };

    // Enable all transitions
    var _transitionsEnabled = function() {
        $('body').removeClass('no-transitions');
    };


    // Sidebars
    // -------------------------

    //
    // On desktop
    //

    // Resize main sidebar
    var _sidebarMainResize = function() {

        // Flip 2nd level if menu overflows
        // bottom edge of browser window
        var revertBottomMenus = function() {
            $('.sidebar-main').find('.nav-sidebar').children('.nav-item-submenu').hover(function() {
                var totalHeight = 0,
                    $this = $(this),
                    navSubmenuClass = 'nav-group-sub',
                    navSubmenuReversedClass = 'nav-item-submenu-reversed';

                totalHeight += $this.find('.' + navSubmenuClass).filter(':visible').outerHeight();
                if($this.children('.' + navSubmenuClass).length) {
                    if(($this.children('.' + navSubmenuClass).offset().top + $this.find('.' + navSubmenuClass).filter(':visible').outerHeight()) > document.body.clientHeight) {
                        $this.addClass(navSubmenuReversedClass)
                    }
                    else {
                        $this.removeClass(navSubmenuReversedClass)
                    }
                }
            });
        }

        // If sidebar is resized by default
        if($('body').hasClass('sidebar-xs')) {
            revertBottomMenus();
        }

        // Toggle min sidebar class
        $('.sidebar-main-toggle').on('click', function (e) {
            e.preventDefault();

            $('body').toggleClass('sidebar-xs').removeClass('sidebar-mobile-main');
            revertBottomMenus();
        });
    };

    // Toggle main sidebar
    var _sidebarMainToggle = function() {
        $(document).on('click', '.sidebar-main-hide', function (e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-main-hidden');
        });
    };

    // Toggle secondary sidebar
    var _sidebarSecondaryToggle = function() {
        $(document).on('click', '.sidebar-secondary-toggle', function (e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-secondary-hidden');
        });
    };


    // Show right, resize main
    var _sidebarRightMainToggle = function() {
        $(document).on('click', '.sidebar-right-main-toggle', function (e) {
            e.preventDefault();

            // Right sidebar visibility
            $('body').toggleClass('sidebar-right-visible');

            // If visible
            if ($('body').hasClass('sidebar-right-visible')) {

                // Make main sidebar mini
                $('body').addClass('sidebar-xs');

                // Hide children lists if they are opened, since sliding animation adds inline CSS
                $('.sidebar-main .nav-sidebar').children('.nav-item').children('.nav-group-sub').css('display', '');
            }
            else {
                $('body').removeClass('sidebar-xs');
            }
        });
    };

    // Show right, hide main
    var _sidebarRightMainHide = function() {
        $(document).on('click', '.sidebar-right-main-hide', function (e) {
            e.preventDefault();

            // Opposite sidebar visibility
            $('body').toggleClass('sidebar-right-visible');
            
            // If visible
            if ($('body').hasClass('sidebar-right-visible')) {
                $('body').addClass('sidebar-main-hidden');
            }
            else {
                $('body').removeClass('sidebar-main-hidden');
            }
        });
    };

    // Toggle right sidebar
    var _sidebarRightToggle = function() {
        $(document).on('click', '.sidebar-right-toggle', function (e) {
            e.preventDefault();

            $('body').toggleClass('sidebar-right-visible');
        });
    };

    // Show right, hide secondary
    var _sidebarRightSecondaryToggle = function() {
        $(document).on('click', '.sidebar-right-secondary-toggle', function (e) {
            e.preventDefault();

            // Opposite sidebar visibility
            $('body').toggleClass('sidebar-right-visible');

            // If visible
            if ($('body').hasClass('sidebar-right-visible')) {
                $('body').addClass('sidebar-secondary-hidden');
            }
            else {
                $('body').removeClass('sidebar-secondary-hidden');
            }
        });
    };


    // Toggle content sidebar
    var _sidebarComponentToggle = function() {
        $(document).on('click', '.sidebar-component-toggle', function (e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-component-hidden');
        });
    };


    //
    // On mobile
    //

    // Expand sidebar to full screen on mobile
    var _sidebarMobileFullscreen = function() {
        $('.sidebar-mobile-expand').on('click', function (e) {
            e.preventDefault();
            var $sidebar = $(this).parents('.sidebar'),
                sidebarFullscreenClass = 'sidebar-fullscreen'

            if(!$sidebar.hasClass(sidebarFullscreenClass)) {
                $sidebar.addClass(sidebarFullscreenClass);
            }
            else {
                $sidebar.removeClass(sidebarFullscreenClass);
            }
        });
    };

    // Toggle main sidebar on mobile
    var _sidebarMobileMainToggle = function() {
        $('.sidebar-mobile-main-toggle').on('click', function(e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-mobile-main').removeClass('sidebar-mobile-secondary sidebar-mobile-right');

            if($('.sidebar-main').hasClass('sidebar-fullscreen')) {
                $('.sidebar-main').removeClass('sidebar-fullscreen');
            }
        });
    };

    // Toggle secondary sidebar on mobile
    var _sidebarMobileSecondaryToggle = function() {
        $('.sidebar-mobile-secondary-toggle').on('click', function (e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-mobile-secondary').removeClass('sidebar-mobile-main sidebar-mobile-right');

            // Fullscreen mode
            if($('.sidebar-secondary').hasClass('sidebar-fullscreen')) {
                $('.sidebar-secondary').removeClass('sidebar-fullscreen');
            }
        });
    };

    // Toggle right sidebar on mobile
    var _sidebarMobileRightToggle = function() {
        $('.sidebar-mobile-right-toggle').on('click', function (e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-mobile-right').removeClass('sidebar-mobile-main sidebar-mobile-secondary');

            // Hide sidebar if in fullscreen mode on mobile
            if($('.sidebar-right').hasClass('sidebar-fullscreen')) {
                $('.sidebar-right').removeClass('sidebar-fullscreen');
            }
        });
    };

    // Toggle component sidebar on mobile
    var _sidebarMobileComponentToggle = function() {
        $('.sidebar-mobile-component-toggle').on('click', function (e) {
            e.preventDefault();
            $('body').toggleClass('sidebar-mobile-component');
        });
    };


    // Navigations
    // -------------------------

    // Sidebar navigation
    var _navigationSidebar = function() {

        // Define default class names and options
        var navClass = 'nav-sidebar',
            navItemClass = 'nav-item',
            navItemOpenClass = 'nav-item-open',
            navLinkClass = 'nav-link',
            navSubmenuClass = 'nav-group-sub',
            navSlidingSpeed = 250;

        // Configure collapsible functionality
        $('.' + navClass).each(function() {
            $(this).find('.' + navItemClass).has('.' + navSubmenuClass).children('.' + navItemClass + ' > ' + '.' + navLinkClass).not('.disabled').on('click', function (e) {
                e.preventDefault();

                // Simplify stuff
                var $target = $(this),
                    $navSidebarMini = $('.sidebar-xs').not('.sidebar-mobile-main').find('.sidebar-main .' + navClass).children('.' + navItemClass);

                // Collapsible
                if($target.parent('.' + navItemClass).hasClass(navItemOpenClass)) {
                    $target.parent('.' + navItemClass).not($navSidebarMini).removeClass(navItemOpenClass).children('.' + navSubmenuClass).slideUp(navSlidingSpeed);
                }
                else {
                    $target.parent('.' + navItemClass).not($navSidebarMini).addClass(navItemOpenClass).children('.' + navSubmenuClass).slideDown(navSlidingSpeed);
                }

                // Accordion
                if ($target.parents('.' + navClass).data('nav-type') == 'accordion') {
                    $target.parent('.' + navItemClass).not($navSidebarMini).siblings(':has(.' + navSubmenuClass + ')').removeClass(navItemOpenClass).children('.' + navSubmenuClass).slideUp(navSlidingSpeed);
                }
            });
        });

        // Disable click in disabled navigation items
        $(document).on('click', '.' + navClass + ' .disabled', function(e) {
            e.preventDefault();
        });

        // Scrollspy navigation
        $('.nav-scrollspy').find('.' + navItemClass).has('.' + navSubmenuClass).children('.' + navItemClass + ' > ' + '.' + navLinkClass).off('click');
    };

    // Navbar navigation
    var _navigationNavbar = function() {

        // Prevent dropdown from closing on click
        $(document).on('click', '.dropdown-content', function(e) {
            e.stopPropagation();
        });

        // Disabled links
        $('.navbar-nav .disabled a, .nav-item-levels .disabled').on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
        });

        // Show tabs inside dropdowns
        $('.dropdown-content a[data-toggle="tab"]').on('click', function(e) {
            $(this).tab('show');
        });
    };


    // Components
    // -------------------------

    // Tooltip
    var _componentTooltip = function() {

        // Initialize
        $('[data-popup="tooltip"]').tooltip();

        // Demo tooltips, remove in production
        var demoTooltipSelector = '[data-popup="tooltip-demo"]';
        if($(demoTooltipSelector).is(':visible')) {
            $(demoTooltipSelector).tooltip('show');
            setTimeout(function() {
                $(demoTooltipSelector).tooltip('hide');
            }, 2000);
        }
    };

    // Popover
    var _componentPopover = function() {
        $('[data-popup="popover"]').popover();
    };


    // Card actions
    // -------------------------

    // Reload card (uses BlockUI extension)
    var _cardActionReload = function() {
        $('.card [data-action=reload]:not(.disabled)').on('click', function (e) {
            e.preventDefault();
            var $target = $(this),
                block = $target.closest('.card');
            
            // Block card
            $(block).block({ 
                message: '<i class="icon-spinner2 spinner"></i>',
                overlayCSS: {
                    backgroundColor: '#fff',
                    opacity: 0.8,
                    cursor: 'wait',
                    'box-shadow': '0 0 0 1px #ddd'
                },
                css: {
                    border: 0,
                    padding: 0,
                    backgroundColor: 'none'
                }
            });

            // For demo purposes
            window.setTimeout(function () {
               $(block).unblock();
            }, 2000); 
        });
    };

    // Collapse card
    var _cardActionCollapse = function() {
        var $cardCollapsedClass = $('.card-collapsed');

        // Hide if collapsed by default
        $cardCollapsedClass.children('.card-header').nextAll().hide();

        // Rotate icon if collapsed by default
        $cardCollapsedClass.find('[data-action=collapse]').addClass('rotate-180');

        // Collapse on click
        $('.card [data-action=collapse]:not(.disabled)').on('click', function (e) {
            var $target = $(this),
                slidingSpeed = 150;

            e.preventDefault();
            $target.parents('.card').toggleClass('card-collapsed');
            $target.toggleClass('rotate-180');
            $target.closest('.card').children('.card-header').nextAll().slideToggle(slidingSpeed);
        });
    };

    // Remove card
    var _cardActionRemove = function() {
        $('.card [data-action=remove]').on('click', function (e) {
            e.preventDefault();
            var $target = $(this),
                slidingSpeed = 150;

            // If not disabled
            if(!$target.hasClass('disabled')) {
                $target.closest('.card').slideUp({
                    duration: slidingSpeed,
                    start: function() {
                        $target.addClass('d-block');
                    },
                    complete: function() {
                        $target.remove();
                    }
                });
            }
        });
    };

    // Card fullscreen mode
    var _cardActionFullscreen = function() {
        $('.card [data-action=fullscreen]').on('click', function (e) {
            e.preventDefault();

            // Define vars
            var $target = $(this),
                cardFullscreen = $target.closest('.card'),
                overflowHiddenClass = 'overflow-hidden',
                collapsedClass = 'collapsed-in-fullscreen',
                fullscreenAttr = 'data-fullscreen';

            // Toggle classes on card
            cardFullscreen.toggleClass('fixed-top h-100 rounded-0');

            // Configure
            if (!cardFullscreen.hasClass('fixed-top')) {
                $target.removeAttr(fullscreenAttr);
                cardFullscreen.children('.' + collapsedClass).removeClass('show');
                $('body').removeClass(overflowHiddenClass);
                $target.siblings('[data-action=move], [data-action=remove], [data-action=collapse]').removeClass('d-none');
            }
            else {
                $target.attr(fullscreenAttr, 'active');
                cardFullscreen.removeAttr('style').children('.collapse:not(.show)').addClass('show ' + collapsedClass);
                $('body').addClass(overflowHiddenClass);
                $target.siblings('[data-action=move], [data-action=remove], [data-action=collapse]').addClass('d-none');
            }
        });
    };


    // Misc
    // -------------------------

    // Dropdown submenus. Trigger on click
    var _dropdownSubmenu = function() {

        // All parent levels require .dropdown-toggle class
        $('.dropdown-menu').find('.dropdown-submenu').not('.disabled').find('.dropdown-toggle').on('click', function(e) {
            e.stopPropagation();
            e.preventDefault();

            // Remove "show" class in all siblings
            $(this).parent().siblings().removeClass('show').find('.show').removeClass('show');

            // Toggle submenu
            $(this).parent().toggleClass('show').children('.dropdown-menu').toggleClass('show');

            // Hide all levels when parent dropdown is closed
            $(this).parents('.show').on('hidden.bs.dropdown', function(e) {
                $('.dropdown-submenu .show, .dropdown-submenu.show').removeClass('show');
            });
        });
    };

    // Header elements toggler
    var _headerElements = function() {

        // Toggle visible state of header elements
        $('.header-elements-toggle').on('click', function(e) {
            e.preventDefault();
            $(this).parents('[class*=header-elements-]').find('.header-elements').toggleClass('d-none');
        });

        // Toggle visible state of footer elements
        $('.footer-elements-toggle').on('click', function(e) {
            e.preventDefault();
            $(this).parents('.card-footer').find('.footer-elements').toggleClass('d-none');
        });
    };


    //
    // Return objects assigned to module
    //

    return {

        // Disable transitions before page is fully loaded
        initBeforeLoad: function() {
            _transitionsDisabled();
        },

        // Enable transitions when page is fully loaded
        initAfterLoad: function() {
            _transitionsEnabled();
        },

        // Initialize all sidebars
        initSidebars: function() {

            // On desktop
            _sidebarMainResize();
            _sidebarMainToggle();
            _sidebarSecondaryToggle();
            _sidebarRightMainToggle();
            _sidebarRightMainHide();
            _sidebarRightToggle();
            _sidebarRightSecondaryToggle();
            _sidebarComponentToggle();

            // On mobile
            _sidebarMobileFullscreen();
            _sidebarMobileMainToggle();
            _sidebarMobileSecondaryToggle();
            _sidebarMobileRightToggle();
            _sidebarMobileComponentToggle();
        },

        // Initialize all navigations
        initNavigations: function() {
            _navigationSidebar();
            _navigationNavbar();
        },

        // Initialize all components
        initComponents: function() {
            _componentTooltip();
            _componentPopover();
        },

        // Initialize all card actions
        initCardActions: function() {
            _cardActionReload();
            _cardActionCollapse();
            _cardActionRemove();
            _cardActionFullscreen();
        },

        // Dropdown submenu
        initDropdownSubmenu: function() {
            _dropdownSubmenu();
        },

        initHeaderElementsToggle: function() {
            _headerElements();
        },

        // Initialize core
        initCore: function() {
            App.initSidebars();
            App.initNavigations();
            App.initComponents();
            App.initCardActions();
            App.initDropdownSubmenu();
            App.initHeaderElementsToggle();
        }
    }
}();


// Initialize module
// ------------------------------

// When content is loaded
document.addEventListener('DOMContentLoaded', function() {
    App.initBeforeLoad();
    App.initCore();
});

// When page is fully loaded
window.addEventListener('load', function() {
    App.initAfterLoad();
});


function afficheTest() {
  var inputStatut = document.getElementById('afficheInput');

  inputStatut.style.visibility="visible";
}
// dark theme switcher //
function dark_toggle() {
    var el1 = document.getElementById("dark-reader");
    if(el1.disabled) {
        el1.disabled = false;
        localStorage.setItem("darkreader", "enabled");
    } else {
        el1.disabled = true;
        localStorage.setItem("darkreader", "disabled");
    }
}

var darkreaderElement = document.getElementById("darkreader");
    if(darkreaderElement){

        if (localStorage.getItem("darkreader") == "enabled") {
            document.getElementById("dark-reader").disabled = false;
        } else {
            document.getElementById("dark-reader").disabled = true;
        }

    }

// font size switcher //
function font_size() {
    var el2 = document.getElementById("new-font-size");
    if(el2.disabled) {
        el2.disabled = false;
        localStorage.setItem("new-font-size", "enabled");
    } else {
        el2.disabled = true;
        localStorage.setItem("new-font-size", "disabled");
    }
}

var newfontsizeElement = document.getElementById("newfontsize");
    if(newfontsizeElement){

        if (localStorage.getItem("newfontsize") == "enabled") {
            document.getElementById("new-font-size").disabled = false;
        } else {
            document.getElementById("new-font-size").disabled = true;
        }
    }

// btn validation //
if ( document.getElementById("displayBlockOn11") ) document.getElementById("displayBlockOn11").addEventListener("click", displayBlockOn0());


function displayBlockOn0() {
    document.getElementById("js-display0").style.display="none";
    document.getElementById("js-display1").style.display="none";
    document.getElementById("js-display2").style.display="none";
    document.getElementById("js-display3").style.display="none";
    document.getElementById("validation0").style.display="block";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="none";
    document.getElementById("js-moove0").style.height="989px";
    document.getElementById("js-moove0").style.width="auto";
    document.getElementById("js-moove0").style.display="block";
    document.getElementById("js-moove1").style.display="none";
}

// btn co-design //
if ( document.getElementById("displayBlockOn22") )
    document.getElementById("displayBlockOn22").addEventListener("click", displayBlockOn1());
function displayBlockOn1() {
    document.getElementById("js-display0").style.display="none";
    document.getElementById("js-display1").style.display="none";
    document.getElementById("js-display2").style.display="none";
    document.getElementById("js-display3").style.display="none";
    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="block";
    document.getElementById("js-moove1").style.height="989px";
    document.getElementById("js-moove1").style.width="auto";
    document.getElementById("js-moove1").style.display="block";
    document.getElementById("js-moove0").style.display="none";
}

// btn ideation-process //

if ( document.getElementById("displayBlockOff0") )
document.getElementById("displayBlockOff0").addEventListener("click", displayBlockOff());
function displayBlockOff() {
    document.getElementById("js-display0").style.display="block";
    document.getElementById("js-display1").style.display="block";
    document.getElementById("js-display2").style.display="block";
    document.getElementById("js-display3").style.display="block";
    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="block";
    document.getElementById("co-design").style.display="none";
    document.getElementById("js-moove0").style.display="block";
    document.getElementById("js-moove0").style.height="auto";
    document.getElementById("js-moove1").style.display="none";
}

/* dom manipulation topic.html menu */

function transformationMenu(that){
    var idMenu = that.id.substr(15);
// btn ideation-process //
if (idMenu <= 0) {
    
  
    document.getElementById("displayBlockOff0").style.backgroundColor="white"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOff0").style.border="1px solid #23C2E7"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="100";
    document.getElementById("displayBlockOff0").style.color="#23C2E7";

    document.getElementById("displayBlockOn11").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn11").style.border="none"; 
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="white";


    document.getElementById("displayBlockOn22").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn22").style.border="none";  
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="white";

    document.getElementById("transform0").style.backgroundColor=""; 
    document.getElementById("transform0").style.marginLeft="2%"; 
    document.getElementById("transform0").style.padding="0%"; 
    document.getElementById("transform0").style.borderLeft="2px solid #999"; 
    document.getElementById("transform0").style.opacity="0.50"; 

    document.getElementById("transform1").style.backgroundColor=""; 
    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("transform2").style.backgroundColor=""; 
    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 

// btn validation //
} if (idMenu == 1) {

    document.getElementById("displayBlockOn11").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn11").style.border="#23C2E7 1px solid";
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="#23C2E7";
 
    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";
 

    document.getElementById("displayBlockOn22").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn22").style.border="none";  
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="white";

    document.getElementById("transform0").style.marginLeft="0"; 
    document.getElementById("transform0").style.padding="2%"; 
    document.getElementById("transform0").style.borderLeft="0"; 
    document.getElementById("transform0").style.opacity="100"; 

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 

// btn co-design //
}  if (idMenu == 2) {
  
    document.getElementById("displayBlockOn22").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn22").style.border="1px solid #23C2E7"; 
    document.getElementById("displayBlockOn22").style.borderBottom="none";
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="#23C2E7";

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";

    document.getElementById("displayBlockOn11").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn11").style.border="none"; 
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="white";

    document.getElementById("transform00").style.marginLeft="0"; 
    document.getElementById("transform00").style.padding="2%"; 
    document.getElementById("transform00").style.borderLeft="0"; 
    document.getElementById("transform00").style.opacity="100"; 

    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 
}}

/* dom manipulation topic.html solution */

function transformation(that){
    var id = that.id;

    // console.log( id );

   /* document.getElementById("transform1").style.marginLeft="0"; 
    document.getElementById("transform1").style.padding="2%"; 
    document.getElementById("transform1").style.borderLeft="0"; 
    document.getElementById("transform1").style.opacity="100"; 
 
    document.getElementById("transform0").style.marginLeft="2%"; 
    document.getElementById("transform0").style.padding="0%"; 
    document.getElementById("transform0").style.borderLeft="2px solid #999"; 
    document.getElementById("transform0").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";

    document.getElementById("displayBlockOn11").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn11").style.border="#23C2E7 1px solid";
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="#23C2E7";

    document.getElementById("displayBlockOn22").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn22").style.border="none";  
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="white";

    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="block";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="none";

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; */

/*
if (id <= 0) {
     
    document.getElementById("transform0").style.marginLeft="0"; 
    document.getElementById("transform0").style.padding="2%"; 
    document.getElementById("transform0").style.borderLeft="0"; 
    document.getElementById("transform0").style.opacity="100"; 

    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";
    
    document.getElementById("displayBlockOn11").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn11").style.border="#23C2E7 1px solid";
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="#23C2E7";

    document.getElementById("displayBlockOn22").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn22").style.border="none";  
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="white";

    document.getElementById("validation0").style.display="block";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="none";

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 


} if (id == 1) {

    document.getElementById("transform1").style.marginLeft="0"; 
    document.getElementById("transform1").style.padding="2%"; 
    document.getElementById("transform1").style.borderLeft="0"; 
    document.getElementById("transform1").style.opacity="100"; 
 
    document.getElementById("transform0").style.marginLeft="2%"; 
    document.getElementById("transform0").style.padding="0%"; 
    document.getElementById("transform0").style.borderLeft="2px solid #999"; 
    document.getElementById("transform0").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";

    document.getElementById("displayBlockOn11").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn11").style.border="#23C2E7 1px solid";
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="#23C2E7";

    document.getElementById("displayBlockOn22").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn22").style.border="none";  
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="white";

    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="block";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="none";

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 

       
} */

/*if (id == 2) {

    document.getElementById("transform2").style.marginLeft="0"; 
    document.getElementById("transform2").style.padding="2%"; 
    document.getElementById("transform2").style.borderLeft="0"; 
    document.getElementById("transform2").style.opacity="100"; 
 
    document.getElementById("transform0").style.marginLeft="2%"; 
    document.getElementById("transform0").style.padding="0%"; 
    document.getElementById("transform0").style.borderLeft="2px solid #999"; 
    document.getElementById("transform0").style.opacity="0.50"; 

    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";

    document.getElementById("displayBlockOn11").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn11").style.border="#23C2E7 1px solid";
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="#23C2E7";

    document.getElementById("displayBlockOn22").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn22").style.border="none";  
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="white";

    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="block";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="none";

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 

   
} if (id == 33) {
     
    document.getElementById("transform00").style.marginLeft="0"; 
    document.getElementById("transform00").style.padding="2%"; 
    document.getElementById("transform00").style.borderLeft="0"; 
    document.getElementById("transform00").style.opacity="100"; 

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";
    
    document.getElementById("displayBlockOn11").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn11").style.border="none"; 
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="white";

    document.getElementById("displayBlockOn22").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn22").style.border="1px solid #23C2E7"; 
    document.getElementById("displayBlockOn22").style.borderBottom="none";
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="#23C2E7";


    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="block";

    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 


} if (id == 11) {

    document.getElementById("transform11").style.marginLeft="0"; 
    document.getElementById("transform11").style.padding="2%"; 
    document.getElementById("transform11").style.borderLeft="0"; 
    document.getElementById("transform11").style.opacity="100"; 
 
    document.getElementById("transform00").style.marginLeft="2%"; 
    document.getElementById("transform00").style.padding="0%"; 
    document.getElementById("transform00").style.borderLeft="2px solid #999"; 
    document.getElementById("transform00").style.opacity="0.50"; 

    document.getElementById("transform22").style.marginLeft="2%"; 
    document.getElementById("transform22").style.padding="0%"; 
    document.getElementById("transform22").style.borderLeft="2px solid #999"; 
    document.getElementById("transform22").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";

    document.getElementById("displayBlockOn11").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn11").style.border="none"; 
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="white";

    document.getElementById("displayBlockOn22").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn22").style.border="1px solid #23C2E7"; 
    document.getElementById("displayBlockOn22").style.borderBottom="none";
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="#23C2E7";


    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="block";

    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 

       
} if (id == 22) {

    document.getElementById("transform22").style.marginLeft="0"; 
    document.getElementById("transform22").style.padding="2%"; 
    document.getElementById("transform22").style.borderLeft="0"; 
    document.getElementById("transform22").style.opacity="100"; 
 
    document.getElementById("transform00").style.marginLeft="2%"; 
    document.getElementById("transform00").style.padding="0%"; 
    document.getElementById("transform00").style.borderLeft="2px solid #999"; 
    document.getElementById("transform00").style.opacity="0.50"; 

    document.getElementById("transform11").style.marginLeft="2%"; 
    document.getElementById("transform11").style.padding="0%"; 
    document.getElementById("transform11").style.borderLeft="2px solid #999"; 
    document.getElementById("transform11").style.opacity="0.50"; 

    document.getElementById("displayBlockOff0").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOff0").style.marginBottom="0"; 
    document.getElementById("displayBlockOff0").style.marginLeft="0"; 
    document.getElementById("displayBlockOff0").style.marginTop="0"; 
    document.getElementById("displayBlockOff0").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOff0").style.paddingLeft="0"; 
    document.getElementById("displayBlockOff0").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOff0").style.border="none"; 
    document.getElementById("displayBlockOff0").style.borderBottom="none"; 
    document.getElementById("displayBlockOff0").style.opacity="0.60";
    document.getElementById("displayBlockOff0").style.color="white";

    document.getElementById("displayBlockOn11").style.backgroundColor="transparent"; 
    document.getElementById("displayBlockOn11").style.marginBottom="0"; 
    document.getElementById("displayBlockOn11").style.marginLeft="0"; 
    document.getElementById("displayBlockOn11").style.marginTop="0"; 
    document.getElementById("displayBlockOn11").style.paddingBottom="0.5%"; 
    document.getElementById("displayBlockOn11").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn11").style.paddingTop="0.5%"; 
    document.getElementById("displayBlockOn11").style.border="none"; 
    document.getElementById("displayBlockOn11").style.borderBottom="none"; 
    document.getElementById("displayBlockOn11").style.opacity="100";
    document.getElementById("displayBlockOn11").style.color="white";

    document.getElementById("displayBlockOn22").style.backgroundColor="white"; 
    document.getElementById("displayBlockOn22").style.marginBottom="0"; 
    document.getElementById("displayBlockOn22").style.marginLeft="0"; 
    document.getElementById("displayBlockOn22").style.marginTop="0"; 
    document.getElementById("displayBlockOn22").style.paddingBottom="1.5%"; 
    document.getElementById("displayBlockOn22").style.paddingLeft="0"; 
    document.getElementById("displayBlockOn22").style.paddingTop="1.5%"; 
    document.getElementById("displayBlockOn22").style.border="1px solid #23C2E7"; 
    document.getElementById("displayBlockOn22").style.borderBottom="none";
    document.getElementById("displayBlockOn22").style.opacity="100";
    document.getElementById("displayBlockOn22").style.color="#23C2E7";

    document.getElementById("validation0").style.display="none";
    document.getElementById("validation1").style.display="none";
    document.getElementById("validation2").style.display="none";
    document.getElementById("ideation-process").style.display="none";
    document.getElementById("co-design").style.display="block";

    document.getElementById("transform1").style.marginLeft="2%"; 
    document.getElementById("transform1").style.padding="0%"; 
    document.getElementById("transform1").style.borderLeft="2px solid #999"; 
    document.getElementById("transform1").style.opacity="0.50"; 

    document.getElementById("transform2").style.marginLeft="2%"; 
    document.getElementById("transform2").style.padding="0%"; 
    document.getElementById("transform2").style.borderLeft="2px solid #999"; 
    document.getElementById("transform2").style.opacity="0.50"; 

   
}*/



}
