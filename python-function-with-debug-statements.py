def my_simple_function():
    logger.info("Entering simple function...")
    if not debug:
        logger.info("We are not debugging!")
        for x in range(3):
            logger.info("NOT DEBUG LOOP %d" % d)
            do_amazing_things()
        logger.info("Finished doing amazing things")
    else:
        logger.info("We ARE debugging!")
        for x in range(3):
            logger.info("DEBUG LOOP %d" % d)
            output = do_amazing_things()
            logger.info("Trying to print our output...")
            logger.info(output)
            logger.info(":( For some reason I never get here ):")
