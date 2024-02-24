import os
import sys
from os.path import isfile, isdir, join
from pathlib import Path

from PIL import Image

OUTPUT_DIR = "out/"
HEADER_BYTES_NUMBER_OF_IMAGES = 4
HEADER_BYTES_WIDTH = 4
HEADER_BYTES_HEIGHT = 4
VALID_SIZES = {(8, 8), (16, 16), (24, 24), (32, 32), (40, 40), (48, 48), (56, 56), (64, 64)}

TEST = "test"

class CustomBinaryImage:
    """
    A class to handle the creation of a custom binary file for images.
    This class allows adding multiple binary images of the same size and saving them as a single custom binary file.
    The binary file will contain a header with metadata about number of images, their width, and height
    """

    output_path: str
    list_of_bytes: list = []
    image_width: int = None
    image_height: int = None

    def __init__(self, working_dir):
        """
        Initializes the CustomBinaryImage instance with the specified working directory.

        :param working_dir: The directory where the output file will be saved.
        """
        self.output_path = working_dir + OUTPUT_DIR

    def add_image(self, image_bytes, width, height):
        """
        Adds an image to the list of images to be saved. All images must have the same dimensions.

        :param image_bytes: Byte representation of the image.
        :param width: The width of the image.
        :param height: The height of the image.
        """

        if self.image_width is not None and self.image_width != width:
            print_usage_and_quit("Sizes don't match - all images must be the same size!")

        self.image_width = width
        self.image_height = height
        self.list_of_bytes.append(image_bytes)

    def save_as(self, file_name="images"):
        """
        Saves the generated custom binary file with the specified file name.

        :param file_name: The name of the file to save the images in (without extension).
        """
        with open(self.output_path + file_name + ".cbi", 'wb') as bin_file:
            # header
            bin_file.write(len(self.list_of_bytes).to_bytes(HEADER_BYTES_NUMBER_OF_IMAGES, byteorder='little'))
            bin_file.write(self.image_width.to_bytes(HEADER_BYTES_WIDTH, byteorder='little'))
            bin_file.write(self.image_height.to_bytes(HEADER_BYTES_HEIGHT, byteorder='little'))

            # data for images
            for image_bytes in self.list_of_bytes:
                bin_file.write(image_bytes)



class ImageConverter:
    """
    A class for converting images into binary and C++ array formats.
    This class handles loading, validating, and converting images to binary and hexadecimal representations.
    It also saves - ready to be used - cpp file containing all relevant data.
    """

    image = None
    data_binary_str: str
    data_bytes: []
    filename: str
    output_path: str

    def __init__(self, working_dir, filename):
        """
        Initializes the ImageConverter instance.

        :param working_dir: The directory where the output file will be saved.
        :param filename: The name of the image file to be converted.
        """

        self.output_path = working_dir + OUTPUT_DIR

        self.image = self.load_image(path + filename)
        self.data_binary_str = self.get_binary_str()
        self.data_bytes = self.get_list_of_bytes()
        self.filename = os.path.splitext(os.path.basename(filename))[0]  # Extract filename without extension

    def load_image(self, image_path):
        """
        Loads an image from a given path and ensures it passes validation and is in grayscale format.

        :param image_path: The path of the image to be loaded.
        :return: A grayscale version of the image.
        """

        try:
            image = Image.open(image_path)
        except Exception as error:
            raise RuntimeError(f"Failed to load image: {error}")

        image = self.convert_to_binary_grayscale(image)
        self.validate_image(image)

        # If I get here, image is good
        return image

    @staticmethod
    def convert_to_binary_grayscale(image):
        """
        Converts an image to grayscale and binary (black and white) format.

        :param image: The image to be converted.
        :return: The converted grayscale image.
        """

        image = image.convert('L')  # Convert to grayscale
        image = image.point(lambda p: 0 if p < 128 else 255, mode='1')  # Black if pixel is <128, else white
        return image

    @staticmethod
    def validate_image(image):
        """
        Validates the image against predefined size constraints and checks if it is strictly black and white.
        Doesn't return anything but instead throws an error.

        :param image: The image to be validated.
        """

        # Size check
        if image.size not in VALID_SIZES:
            valid_sizes_str = ', '.join([f"{width}x{height}"
                                         for width, height in sorted(list(VALID_SIZES))])
            raise ValueError(f"Image dimensions must be one of the following: {valid_sizes_str}.")

        # Grayscale check
        colors = image.getcolors()
        if len(colors) > 2 or any(c[1] != 0 and c[1] != 255 for c in colors):
            print(f"Detected colors in the image: {colors}")
            raise ValueError("Image must be strictly black and white.")

    def get_binary_str(self):
        """
        Converts the image to a binary string representation.

        :return: A string representing the binary data of the image.
        """

        data = ""
        for y in range(self.image.height):
            row_data = ""
            for x in range(self.image.width):
                pixel_value = self.image.getpixel((x, y))
                row_data += "0" if pixel_value == 255 else "1"
            # Left pad the row data with 0s to make it a multiple of 8 bits
            row_data = row_data.ljust(8 * ((len(row_data) + 7) // 8), "0")
            data += row_data

        return data

    def get_list_of_bytes(self):
        """
        Converts the binary string data to a list of byte values.

        :return: A list of byte values representing the image.
        """

        byte_data = []
        # Break the binary_data into chunks of 8 and convert each chunk to a byte
        for i in range(0, len(self.data_binary_str), 8):
            byte_chunk = self.data_binary_str[i:i + 8]
            byte_value = int(byte_chunk, 2)
            byte_data.append(byte_value)

        return byte_data

    def display_data(self):
        """
        Displays the binary and hexadecimal representations of the image along with some stats.

        Only used for feedback during development.
        """

        num_ones = self.data_binary_str.count("1")
        num_zeros = self.data_binary_str.count("0")
        total_bits = len(self.data_binary_str)
        total_bytes = len(self.data_bytes)

        formatted_binary = '\n'.join([self.data_binary_str[i:i + self.image.size[0]]
             for i in range(0, len(self.data_binary_str), self.image.size[0])])

        formatted_bytes = ', '.join([f"0x{byte:02X}" for byte in self.data_bytes])

        formatted_replaced_binary = formatted_binary.replace('0', ' ').replace('1', '#')

        print("Binary Representation:\n", formatted_binary)
        print("Binary (cooler) Representation:\n", formatted_replaced_binary)
        print("Hexadecimal Representation:\n", formatted_bytes)
        print(f"Total Number of '1's: {num_ones}")
        print(f"Total Number of '0's: {num_zeros}")
        print(f"Total Bits: {total_bits}")
        print(f"Total Bytes Used: {total_bytes}")
        print(f"Name: {self.filename}")

    def generate_cpp_format(self):
        """
        Generates a C++ array format of the image data.

        :return: A string containing the C++ array representation of the image.
        """

        # Calculate the number of bytes per row
        bytes_per_row = self.image.size[0] // 8  # width divided by 8

        cpp_str = f"const uint8_t {self.filename}_map[] PROGMEM =\n{{\n"
        cpp_str += f"    //Image: {self.filename}\n"
        cpp_str += f"    //Size: {self.image.size[0]}x{self.image.size[1]}\n\n"


        for i in range(0, len(self.data_bytes), bytes_per_row):
            cpp_str += "    " + ', '.join([f"0x{byte:02X}" for byte in self.data_bytes[i:i + bytes_per_row]]) + ",\n"

        cpp_str = cpp_str.rstrip(",\n") + "\n};\n"


        cpp_str += "\n\n"

        cpp_str += f"sImage {self.filename} = {{\n"
        cpp_str += f"    {self.filename}_map,\n"
        cpp_str += f"    {self.image.size[0]} /* Width */,\n"
        cpp_str += f"    {self.image.size[1]} /*Height */ \n"
        cpp_str += "}};"

        return cpp_str


    def save_data(self):
        """
        Saves the C++ array format of the image to a file.
        """

        # Create a dir if doesn't exist
        Path(self.output_path).mkdir(parents=True, exist_ok=True)

        cpp_filename = self.output_path + self.filename + ".cpp"
        text = self.generate_cpp_format()

        with open(cpp_filename, 'w') as f:
            f.write(text)

        print("Data saved as cpp format in the file ", cpp_filename)


def print_usage_and_quit(error):
    """
    Prints an error message and usage instructions, then exits the program.

    :param error: The error message to be printed.
    """

    print(f"Error: {error}")
    print("Usage1 - single file: convert.py <image_path>")
    print("Usage2 - bulk convert: convert.py <folder_with_images_path>")
    sys.exit(1)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print_usage_and_quit("Invalid argument")

    try:
        path = sys.argv[1]

        if isfile(path):
            converter = ImageConverter(path)
            converter.display_data()
            converter.save_data()
        elif isdir(path):
            print("Folder contains images")
            # if path doesn't end with / ... add it?
            if not path.endswith("/"):
                path += "/"

            cbi = CustomBinaryImage(path)

            files = [f for f in os.listdir(path) if isfile(join(path, f)) and f.endswith('.bmp')]
            files = sorted(files)

            # TODO stop when there are no files

            for file in files:
                print("Converting: " + file)
                file_path = path + "/" + file
                converter = ImageConverter(path, file)
                # converter.display_data()
                converter.save_data()

                cbi.add_image(bytes(converter.data_bytes), converter.image.width, converter.image.height)

            # Save cbi
            cbi.save_as()

        else:
            print_usage_and_quit("Not file or directory!")

    except Exception as e:
        print(f"Error: {e}")
