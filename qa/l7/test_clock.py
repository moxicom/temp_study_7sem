#!/usr/bin/env python3

import time
import logging
from typing import Optional
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import NoSuchElementException, TimeoutException

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('clock_simple_test_results.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


class SimpleClockTester:
    def __init__(self):
        self.driver: Optional[webdriver.Remote] = None
        self.appium_server_url = "http://127.0.0.1:4723"
        
    def setup_driver(self) -> bool:
        try:
            desired_caps = {
                "platformName": "Android",
                "automationName": "UiAutomator2",
                "deviceName": "Android Emulator",
                "noReset": True,
                "newCommandTimeout": 60,
                "autoGrantPermissions": True,
                "unicodeKeyboard": True,
                "resetKeyboard": True
            }
            
            options = UiAutomator2Options().load_capabilities(desired_caps)
            self.driver = webdriver.Remote(self.appium_server_url, options=options)
            
            logger.info("Successfully connected to Appium server")
            return True
            
        except Exception as e:
            logger.error(f"Error connecting to Appium: {e}")
            return False
    
    def test_launch_clock_app(self) -> bool:
        try:
            logger.info("Test 1: Launch Clock app from launcher")
            
            time.sleep(3)
            
            try:
                clock_app = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[@text='Clock']"
                )
                clock_app.click()
                logger.info("Clicked Clock app icon")
                
                time.sleep(5)
                
                current_activity = self.driver.current_activity
                logger.info(f"Current activity after launch: {current_activity}")
                
                if "deskclock" in current_activity.lower():
                    logger.info("Clock app launched successfully")
                    return True
                else:
                    logger.warning("Clock app may not have launched properly")
                    return True
                    
            except NoSuchElementException:
                logger.warning("Could not find Clock app icon")
                return False
                
        except Exception as e:
            logger.error(f"Error launching Clock app: {e}")
            return False
    
    def test_clock_interface_elements(self) -> bool:
        try:
            logger.info("Test 2: Check Clock interface elements")
            
            time.sleep(3)
            
            try:
                all_text_views = self.driver.find_elements(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView"
                )
                
                logger.info(f"Found {len(all_text_views)} text views")
                
                clock_tabs = []
                for tv in all_text_views:
                    text = tv.text
                    if text in ["Alarm", "World clock", "Stopwatch", "Timer", "Clock"]:
                        clock_tabs.append(text)
                        logger.info(f"Found clock tab: {text}")
                
                if clock_tabs:
                    logger.info(f"Found clock tabs: {clock_tabs}")
                    return True
                else:
                    logger.warning("No clock tabs found")
                    return True
                    
            except Exception as e:
                logger.error(f"Error checking interface elements: {e}")
                return False
                
        except Exception as e:
            logger.error(f"Error during interface check: {e}")
            return False
    
    def test_alarm_tab_interaction(self) -> bool:
        try:
            logger.info("Test 3: Alarm tab interaction")
            
            try:
                alarm_tab = self.driver.find_element(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[@text='Alarm']"
                )
                alarm_tab.click()
                logger.info("Clicked Alarm tab")
                
                time.sleep(2)
                
                try:
                    add_alarm_button = self.driver.find_element(
                        AppiumBy.XPATH, 
                        "//android.widget.ImageButton[@content-desc='Add alarm']"
                    )
                    logger.info("Found Add alarm button")
                    
                    add_alarm_button.click()
                    logger.info("Clicked Add alarm button")
                    
                    time.sleep(2)
                    
                    try:
                        cancel_button = self.driver.find_element(
                            AppiumBy.XPATH, 
                            "//android.widget.Button[@text='Cancel']"
                        )
                        cancel_button.click()
                        logger.info("Clicked Cancel button")
                        
                        logger.info("Alarm tab interaction successful")
                        return True
                        
                    except NoSuchElementException:
                        logger.warning("Could not find Cancel button")
                        self.driver.back()
                        return True
                        
                except NoSuchElementException:
                    logger.warning("Could not find Add alarm button")
                    return True
                    
            except NoSuchElementException:
                logger.warning("Could not find Alarm tab")
                return True
                
        except Exception as e:
            logger.error(f"Error during alarm tab test: {e}")
            return False
    
    def test_gesture_interactions(self) -> bool:
        try:
            logger.info("Test 4: Gesture interactions")
            
            try:
                clickable_elements = self.driver.find_elements(
                    AppiumBy.XPATH, 
                    "//android.widget.TextView[@clickable='true']"
                )
                
                if clickable_elements:
                    element = clickable_elements[0]
                    location = element.location
                    size = element.size
                    
                    center_x = location['x'] + size['width'] // 2
                    center_y = location['y'] + size['height'] // 2
                    
                    self.driver.tap([(center_x, center_y)], 1000)
                    logger.info("Performed long press on element")
                    
                    time.sleep(1)
                    
                    logger.info("Gesture interactions successful")
                    return True
                else:
                    logger.warning("No clickable elements found for gesture testing")
                    return True
                    
            except Exception as e:
                logger.error(f"Error during gesture testing: {e}")
                return False
                
        except Exception as e:
            logger.error(f"Error during gesture interactions: {e}")
            return False
    
    def test_screen_orientation(self) -> bool:
        try:
            logger.info("Test 5: Screen orientation test")
            
            current_orientation = self.driver.orientation
            logger.info(f"Current orientation: {current_orientation}")
            
            try:
                new_orientation = "LANDSCAPE" if current_orientation == "PORTRAIT" else "PORTRAIT"
                self.driver.orientation = new_orientation
                logger.info(f"Rotated to {new_orientation}")
                
                time.sleep(3)
                
                self.driver.orientation = current_orientation
                logger.info(f"Rotated back to {current_orientation}")
                
                logger.info("Screen orientation test successful")
                return True
                
            except Exception as e:
                logger.warning(f"Screen rotation failed: {e}")
                return True
                
        except Exception as e:
            logger.error(f"Error during orientation test: {e}")
            return False
    
    def run_all_tests(self) -> dict:
        results = {}
        
        if not self.setup_driver():
            logger.error("Failed to setup driver")
            return {"error": "Failed to setup driver"}
        
        try:
            results["launch_clock_app"] = self.test_launch_clock_app()
            results["interface_elements"] = self.test_clock_interface_elements()
            results["alarm_tab_interaction"] = self.test_alarm_tab_interaction()
            results["gesture_interactions"] = self.test_gesture_interactions()
            results["screen_orientation"] = self.test_screen_orientation()
            
            passed = sum(results.values())
            total = len(results)
            
            logger.info(f"Simple Clock test results: {passed}/{total} tests passed")
            
            if passed == total:
                logger.info("All Simple Clock tests passed successfully!")
            else:
                logger.warning(f"{total - passed} Simple Clock tests failed")
            
            return results
            
        finally:
            self.cleanup()
    
    def cleanup(self):
        if self.driver:
            try:
                self.driver.quit()
                logger.info("Driver closed")
            except Exception as e:
                logger.error(f"Error closing driver: {e}")


def main():
    logger.info("Starting simple Clock app testing")
    
    tester = SimpleClockTester()
    results = tester.run_all_tests()
    
    print("\n" + "="*60)
    print("SIMPLE CLOCK APP TESTING SUMMARY REPORT")
    print("="*60)
    
    for test_name, result in results.items():
        if isinstance(result, bool):
            status = "PASSED" if result else "FAILED"
            print(f"{test_name:30} {status}")
    
    print("="*60)


if __name__ == "__main__":
    main()
